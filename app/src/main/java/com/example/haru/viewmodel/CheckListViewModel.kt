package com.example.haru.viewmodel

import android.security.KeyChainAliasCallback
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.*
import com.example.haru.data.repository.TagRepository
import com.example.haru.data.repository.TodoRepository
import com.example.haru.utils.FormatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CheckListViewModel() :
    ViewModel() {
    private val todoRepository = TodoRepository()
    private val tagRepository = TagRepository()

    private val basicTag = listOf<Tag>(Tag("완료", "완료"), Tag("미분류", "미분류"))
    var clickedTag: Int? = null

    private val _todoDataList = MutableLiveData<List<Todo>>()
    private val _tagDataList = MutableLiveData<List<Tag>>()
    private val _flaggedTodos = MutableLiveData<List<Todo>>()
    private val _taggedTodos = MutableLiveData<List<Todo>>()
    private val _untaggedTodos = MutableLiveData<List<Todo>>()
    private val _completedTodos = MutableLiveData<List<Todo>>()

    private val _todoByTag = MutableLiveData<Boolean>()
    val todoByTag: LiveData<Boolean> = _todoByTag

    private val _addTodoId = MutableLiveData<String>()
    val addTodoId: LiveData<String> = _addTodoId

    private val _todayTodo = MutableLiveData<List<Todo>>()
    val todayTodo: LiveData<List<Todo>> get() = _todayTodo
    private val todayList = mutableListOf<Todo>()

    private val todoList = mutableListOf<Todo>()
    val todoDataList: LiveData<List<Todo>> get() = _todoDataList
    val tagDataList: LiveData<List<Tag>> get() = _tagDataList
    val flaggedTodos: LiveData<List<Todo>> get() = _flaggedTodos
    val taggedTodos: LiveData<List<Todo>> get() = _taggedTodos
    val untaggedTodos: LiveData<List<Todo>> get() = _untaggedTodos
    val completedTodos: LiveData<List<Todo>> get() = _completedTodos

    var todoByTagItem: String? = null

    var tagInputString: String = ""

    init {
        getTodoMain {
            flaggedTodos.value?.let { todoList.addAll(it) }
            taggedTodos.value?.let { todoList.addAll(it) }
            untaggedTodos.value?.let { todoList.addAll(it) }
            completedTodos.value?.let { todoList.addAll(it) }
            _todoDataList.postValue(todoList)
        }
        getTag()
    }

    fun setVisibility(str: String, type: Int) { // Section Toggle 기능
        if (type == 0) {
            val index = todoList.indexOf(Todo(type = 4, content = str))
            for (i in index + 1 until todoList.size) {
                if (todoList[i].type == 6)
                    break
                else if (todoList[i].type == 3)
                    break
                val todo = todoList[i].copy(visibility = !todoList[i].visibility)
                todoList[i] = todo
            }
            _todoDataList.value = todoList
        } else {
            val index = todayList.indexOf(Todo(type = 4, content = str))
            for (i in index + 1 until todayList.size) {
                if (todayList[i].type == 3)
                    break
                val todo = todayList[i].copy(visibility = !todayList[i].visibility)
                todayList[i] = todo
            }
            _todayTodo.value = todayList
        }
    }

    fun clearToday() {
        todayList.clear()
    }

    fun getTodoList(): List<Todo> {
        return todoList
    }

    /* -------------------------------------태그 관련 기능---------------------------------- */

    fun getTag() { // Tag 받아오는 기능
        viewModelScope.launch {
            tagRepository.getTag {
                if (it?.success == true)
                    _tagDataList.postValue(basicTag + it.data)
                else Log.e("20191627", it.toString())
            }
        }
    }

    fun readyCreateTag(string: String): String? { // 태그 생성전 검사 기능
        tagInputString = string.trim()
        return if (tagInputString.contains(" "))
            null
        else
            return tagInputString

    }

    fun createTag(content: Content) {  // 태그 생성 기능
        viewModelScope.launch {
            tagRepository.createTag(content = content) {
                if (it?.success == true) {
                    getTag()
                    withTagUpdate()
                } else {
                    Log.e("20191627", it.toString())
                }
            }
        }
    }

    fun deleteTagList(tagIdList: TagIdList, callback: () -> Unit) {  // 태그 삭제 기능
        viewModelScope.launch {
            tagRepository.deleteTagList(tagIdList = tagIdList) {
                if (it?.success == true) {
                    getTag()
                    withTagUpdate()
                } else Log.e("20191627", it.toString())
                callback()
            }
        }
    }

    fun updateTag(tagId: String, updateTag: TagUpdate, callback: () -> Unit) {  // 태그 수정 기능
        viewModelScope.launch {
            tagRepository.updateTag(tagId = tagId, updateTag = updateTag) {
                if (it?.success == true) {
                    getTag()
                    withTagUpdate()
                } else Log.e("20191627", it.toString())
                callback()
            }
        }
    }

    /* -------------------------------------------------------------------------- */

    fun withTagUpdate() {  // Todo에 변동을 주는 기능을 하면 TodoData를 업데이트 해주는 기능
        if (todoByTag.value == true) {
            when (todoByTagItem) {
                null -> {}
                "중요" -> getTodoByFlag()
                "완료" -> getTodoByTag(1)
                "미분류" -> getTodoByTag(2)
                else -> {
                    val tag = tagDataList.value!!.find { it.content == todoByTagItem }
                    val i = tagDataList.value!!.indexOf(tag)
                    getTodoByTag(i + 1)
                }
            }
            return
        }
        getTodoMain {
            _todoByTag.postValue(false)
            todoByTagItem = null
            todoList.clear()
            flaggedTodos.value?.let { todoList.addAll(it) }
            taggedTodos.value?.let { todoList.addAll(it) }
            untaggedTodos.value?.let { todoList.addAll(it) }
            completedTodos.value?.let { todoList.addAll(it) }
            _todoDataList.postValue(todoList)
        }
    }

    fun checkTodayMode() {
        if (todayList.isNotEmpty()) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23) // 시간을 23시로 설정
                set(Calendar.MINUTE, 59) // 분을 59분으로 설정
                set(Calendar.SECOND, 59) // 초를 59초로 설정
            }
            val todayFrontEndDate = FrontEndDate(FormatDate.dateToStr(calendar.time)!!)
            getTodayTodo(todayFrontEndDate) {}
        }
    }

    fun getTodoMain(callback: () -> Unit) {  // 메인 화면에서 보여줄 TodoData를 가져오는 기능
        viewModelScope.launch {
            todoRepository.getTodoMain {
                if (it?.success == true) {
                    todoList.clear()
                    _todoByTag.postValue(false)
                    todoByTagItem = null

                    if (it.data.flaggedTodos.isNotEmpty())
                        _flaggedTodos.postValue(
                            listOf(Todo(type = 4, content = "중요"))
                                    + it.data.flaggedTodos + listOf(Todo(type = 3))
                        )
                    else _flaggedTodos.postValue(
                        listOf(
                            Todo(type = 4, content = "중요"),
                            Todo(type = 5, content = "중요한 할 일이 있나요?"), Todo(type = 3)
                        )
                    )

                    if (it.data.taggedTodos.isNotEmpty())
                        _taggedTodos.postValue(
                            listOf(
                                Todo(
                                    type = 4,
                                    content = "분류"
                                )
                            ) + it.data.taggedTodos + listOf(Todo(type = 3))
                        ) else _taggedTodos.postValue(
                        listOf(
                            Todo(type = 4, content = "분류"),
                            Todo(type = 5, content = "모든 할 일을 마쳤습니다!"),
                            Todo(type = 3)
                        )
                    )
                    if (it.data.untaggedTodos.isNotEmpty())
                        _untaggedTodos.postValue(
                            listOf(
                                Todo(
                                    type = 4,
                                    content = "미분류"
                                )
                            ) + it.data.untaggedTodos + listOf(Todo(type = 3))
                        ) else _untaggedTodos.postValue(
                        listOf(
                            Todo(type = 4, content = "미분류"),
                            Todo(type = 5, content = "모든 할 일을 마쳤습니다!"),
                            Todo(type = 3)
                        )
                    )
                    if (it.data.completedTodos.isNotEmpty())
                        _completedTodos.postValue(
                            listOf(
                                Todo(
                                    type = 4,
                                    content = "완료"
                                )
                            ) + it.data.completedTodos + listOf(Todo(type = 6))
                        ) else _completedTodos.postValue(
                        listOf(
                            Todo(type = 4, content = "완료"),
                            Todo(type = 5, content = "할일을 완료해 보세요!"),
                            Todo(type = 6)
                        )
                    )

                    _todoByTag.postValue(false)
                    todoByTagItem = null
                } else {
                    Log.e("20191627", "CheckListViewModel -> GetTodoMain Fail")
                    Log.e("20191627", it.toString())
                }
            }
            callback()
        }
    }

    fun getTodayTodo(
        frontEndDate: FrontEndDate,
        callback: () -> Unit
    ) { // Today 창에서 보여줄 Todo를 가져오는 기능
        viewModelScope.launch {
            todoRepository.getTodayTodo(frontEndDate = frontEndDate) {
                if (it?.success == true) {
                    todayList.clear()
                    todayList.apply {
                        this.add(Todo(type = 4, content = "중요"))
                        if (it.data.flaggedTodos.isNotEmpty())
                            this.addAll(it.data.flaggedTodos)
                        else this.add(Todo(type = 5, content = "중요한 할 일이 있나요?"))

                        this.add(Todo(type = 3))

                        this.add(Todo(type = 4, content = "오늘 할 일"))
                        if (it.data.todayTodos.isNotEmpty())
                            this.addAll(it.data.todayTodos)
                        else this.add(Todo(type = 5, content = "모든 할 일을 마쳤습니다!"))

                        this.add(Todo(type = 3))

                        this.add(Todo(type = 4, content = "오늘 마감"))
                        if (it.data.endDatedTodos.isNotEmpty())
                            this.addAll(it.data.endDatedTodos)
                        else this.add(Todo(type = 5, content = "모든 할 일을 마쳤습니다!"))

                        this.add(Todo(type = 3))

                        this.add(Todo(type = 4, content = "완료"))
                        if (it.data.completedTodos.isNotEmpty())
                            this.addAll(it.data.completedTodos + Todo(type = 6))
                        else this.addAll(listOf(Todo(type = 5, content = "할일을 완료해 보세요!"), Todo(type = 6)))
                    }
                    _todayTodo.postValue(todayList)
                } else {
                    Log.e("20191627", "CheckListViewModel -> GetTodayTdo Fail")
                    Log.e("20191627", it.toString())
                }
                callback()
            }
        }
    }


    fun addTodo(
        todoRequest: TodoRequest,
        calendar: Boolean = false,
        callback: () -> Unit
    ) { // Todo 추가 기능
        viewModelScope.launch {
            todoRepository.createTodo(calendar, todoRequest) {
                if (it?.success == true) {
                    getTag()
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.e("20191627", "CheckListViewModel -> AddTodo Fail")
                    Log.e("20191627", it.toString())
                }
            }
            callback()
        }
    }

    fun getTodoByTag(position: Int) { // Tag 정렬 기능
        viewModelScope.launch {
            todoByTagItem = tagDataList.value!![position - 1].content
            _todoByTag.value = true
            todoList.apply {
                clear()
                when (position) {
                    1 -> {
                        todoRepository.getTodoByComplete {
                            if (it?.success == true){
                                this.addAll(
                                    listOf(
                                        Todo(
                                            type = 4,
                                            content = todoByTagItem!!
                                        )
                                    ) + it.data!!
                                )
                                if (it.data.isEmpty())
                                    this.add(Todo(type = 5, content = "모든 할 일을 마쳤습니다!"))
                            }
                        }
                    }
                    2 -> {
                        todoRepository.getTodoByUntag {
                            if (it?.success == true){
                                this.addAll(listOf(Todo(type = 4,content = todoByTagItem!!))
                                        + it.data!!)
                                if (it.data.isEmpty())
                                    this.add(Todo(type = 5, content = "모든 할 일을 마쳤습니다!"))
                            }
                        }
                    }
                    else -> {
                        todoRepository.getTodoByTag(tagDataList.value!![position - 1].id) {
                            if (it?.success == true) {
                                this.add(Todo(type = 4, content = "중요"))
                                if (it.data.flaggedTodos.isNotEmpty())
                                    this.addAll(it.data.flaggedTodos)
                                else this.add(Todo(type = 5, content = "중요한 할 일이 있나요?"))
                                this.add(Todo(type = 3))

                                this.add(Todo(type = 4, content = todoByTagItem!!))
                                if (it.data.unFlaggedTodos.isNotEmpty())
                                    this.addAll(it.data.unFlaggedTodos)
                                else this.add(Todo(type = 5, content = "모든 할 일을 마쳤습니다!"))
                                this.add(Todo(type = 3))

                                this.add(Todo(type = 4, content = "완료"))
                                if (it.data.completedTodos.isNotEmpty())
                                    this.addAll(it.data.completedTodos)
                                else this.add(Todo(type = 5, content = "할일을 완료해 보세요!"))
                            }
                        }
                    }
                }
                add(Todo(type = 6))
            }
            _todoDataList.value = todoList
        }
    }

    fun getTodoByFlag() {  // 중요 표시된 Todo 가져오는 기능
        viewModelScope.launch {
            todoByTagItem = "중요"
            _todoByTag.value = true
            todoList.apply {
                clear()
                todoRepository.getTodoByFlag {
                    if (it?.success == true){
                        addAll(listOf(Todo(type = 4, content = "중요")) + it.data!!)
                        if (it.data.isEmpty())
                            add(Todo(type = 5, content = "중요한 할 일이 있나요?"))
                    }
                    else Log.e("20191627", it.toString())
                }
            }
            _todoDataList.value = todoList
        }
    }

    fun clear() {
        _flaggedTodos.value = emptyList()
        _taggedTodos.value = emptyList()
        _untaggedTodos.value = emptyList()
        _completedTodos.value = emptyList()
    }

    /* ------------------------------------할 일의 수정 기능--------------------------------- */

    // 반복하지 않는 Todo를 수정하거나, 반복하는 Todo의 전체를 수정하는 기능
    fun updateTodo(todoId: String, todo: UpdateTodo, callback: () -> Unit) {
        viewModelScope.launch {
            val updateTodo = todoRepository.updateTodo(todoId = todoId, todo = todo) {
                if (it?.success == true) {
                    getTag()
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.e("20191627", "CheckListViewModel -> UpdateTodo Fail")
                    Log.e("20191627", it.toString())
                }
                callback()
            }
        }
    }

    // 반복하는 할 일의 front부분을 수정 기능
    fun updateRepeatFrontTodo(
        todoId: String,
        updateRepeatFrontTodo: UpdateRepeatFrontTodo,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val updateRepeatTodo = todoRepository.updateRepeatFrontTodo(
                todoId = todoId,
                updateRepeatFrontTodo = updateRepeatFrontTodo
            ) {
                if (it?.success == true) {
                    getTag()
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.e("20191627", "CheckListViewModel -> UpdateRepeatFrontTodo Fail")
                    Log.e("20191627", it.toString())
                }
                callback()
            }
        }
    }

    fun updateRepeatMiddleTodo(
        todoId: String,
        updateRepeatMiddleTodo: UpdateRepeatMiddleTodo,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val updateRepeatTodo = todoRepository.updateRepeatMiddleTodo(
                todoId = todoId,
                updateRepeatMiddleTodo = updateRepeatMiddleTodo
            ) {
                if (it?.success == true) {
                    getTag()
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.e("20191627", "CheckListViewModel -> UpdateRepeatMiddleTodo Fail")
                    Log.e("20191627", it.toString())
                }
                callback()
            }

        }

    }

    fun updateRepeatBackTodo(
        todoId: String,
        updateRepeatBackTodo: UpdateRepeatBackTodo, callback: () -> Unit
    ) {
        viewModelScope.launch {
            val updateRepeatTodo = todoRepository.updateRepeatBackTodo(
                todoId = todoId,
                updateRepeatBackTodo = updateRepeatBackTodo
            ) {
                if (it?.success == true) {
                    getTag()
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.e("20191627", "CheckListViewModel -> UpdateRepeatBackTodo Fail")
                    Log.e("20191627", it.toString())
                }
                callback()
            }
        }
    }
    /* -------------------------------------------------------------------------------- */

    /* ----------------------------------할 일 삭제 기능---------------------------------- */
    // 반복하지 않는 Todo, 반복하는 Todo의 전체를 삭제하는 기능
    fun deleteTodo(
        todoId: String,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val successData = todoRepository.deleteTodo(todoId = todoId) {
                if (it?.success == true) {
                    checkTodayMode()
                    withTagUpdate()
                }
                callback()
            }
        }
    }

    // 반복하는 할일의 front를 삭제하는 기능
    fun deleteRepeatFrontTodo(
        todoId: String,
        frontEndDate: FrontEndDate,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val successData = todoRepository.deleteRepeatFrontTodo(todoId, frontEndDate) {
                if (it?.success == true) {
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.e("20191627", "CheckListViewModel -> DeleteRepeatFrontTodo Fail")
                    Log.e("20191627", it.toString())
                }
                callback()
            }
        }
    }

    fun deleteRepeatMiddleTodo(
        todoId: String,
        middleEndDate: MiddleEndDate,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val successData = todoRepository.deleteRepeatMiddleTodo(todoId, middleEndDate) {
                if (it?.success == true) {
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.e("20191627", "CheckListViewModel -> DeleteRepeatMiddleTodo Fail")
                    Log.e("20191627", it.toString())
                }
                callback()
            }
        }
    }

    fun deleteRepeatBackTodo(
        todoId: String,
        backRepeatEnd: BackRepeatEnd,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val successData = todoRepository.deleteRepeatBackTodo(todoId, backRepeatEnd) {
                if (it?.success == true) {
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.e("20191627", "CheckListViewModel -> DeleteRepeatBackTodo Fail")
                    Log.e("20191627", it.toString())
                }
                callback()
            }
        }
    }

    /* -------------------------------------------------------------------------------- */


    /* ------------------------------------할 일의 완료 기능--------------------------------- */

    // 하위 투두를 완료하는 기능
    fun completeSubTodo(completed: Completed, id: String, subTodoId: String, subTodoPosition: Int) {
        viewModelScope.launch {
            val successData = todoRepository.completeSubTodo(
                subTodoId = subTodoId,
                completed = completed
            ) {
                if (it?.success == true) {
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.e("20191627", "CheckListViewModel -> CompleteSubTodo Fail")
                    Log.e("20191627", it.toString())
                }
            }
        }
    }

    // 반복하지 않는 할 일, 반복하는 할 일의 전체를 완료하는 기능
    fun completeNotRepeatTodo(
        completed: Completed,
        id: String,
        callback: (completed: Completed, successData : SuccessFail?) -> Unit
    ) {
        viewModelScope.launch {
            val successData = todoRepository.completeNotRepeatTodo(
                todoId = id,
                completed = completed
            ) {
                Log.e("20191627", it.toString())
                if (it?.success == true) {
                    checkTodayMode()
                    withTagUpdate()
                }
                callback(completed, it)
            }
        }
    }

    // 반복하는 할 일의 front를 완료하는 기능
    fun completeRepeatFrontTodo(id: String, frontEndDate: FrontEndDate, callback: (successData : SuccessFail?) -> Unit) {
        viewModelScope.launch {
            val successData =
                todoRepository.completeRepeatFrontTodo(todoId = id, frontEndDate = frontEndDate) {
                    if (it?.success == true) {
                        checkTodayMode()
                        withTagUpdate()
                    } else {
                        Log.e("20191627", "CheckListViewModel -> CompleteRepeatFrontTodo Fail")
                        Log.e("20191627", it.toString())
                    }
                    callback(it)
                }
        }
    }

    /* -------------------------------------------------------------------------------- */


    // 하위 투두가 있는 할 일의 Toggle을 하는 기능
    fun updateFolded(folded: Folded, id: String) {
        viewModelScope.launch {
            val successData = todoRepository.updateFolded(
                todoId = id,
                folded = folded
            ) {
                if (it?.success == true) {
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.e("20191627", "CheckListViewModel -> UpdateFolded Fail")
                    Log.e("20191627", it.toString())
                }
            }
        }
    }

    // Todo의 중요를 업데이트 하는 기능
    fun updateFlag(flag: Flag, id: String, callback: (flag: Flag, successData : SuccessFail?) -> Unit) {
        viewModelScope.launch {
            val successData =
                todoRepository.updateFlag(todoId = id, flag = flag) {
                    if (it?.success == true) {
                        checkTodayMode()
                        withTagUpdate()
                    }
                    callback(flag, it)
                }
        }
    }

    fun updateOrderMainTodo(
        changeOrderTodo: ChangeOrderTodo
    ) {
        viewModelScope.launch {
            val successData = todoRepository.updateOrderMainTodo(changeOrderTodo)
            // successData가 null이면 실패임
            if (successData?.success != true) {
                Log.e("20191627", "CheckListViewModel -> UpdateOrderMainTodo Fail")
                Log.e("20191627", successData.toString())
            }
        }
    }
}
