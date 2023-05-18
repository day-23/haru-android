package com.example.haru.viewmodel

import android.security.KeyChainAliasCallback
import android.util.Log
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
            Log.d("20191627", str)
            Log.d("20191627", index.toString())
            for (i in index + 1 until todoList.size) {
                if (todoList[i].type == 6)
                    break
                else if (todoList[i].type == 3)
                    break
                val todo = todoList[i].copy(visibility = !todoList[i].visibility)
                todoList[i] = todo
                Log.d("20191627", todoList[i].visibility.toString())
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
                if (it != null)
                    _tagDataList.postValue(basicTag + it)
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
                if (it != null) {
                    getTag()
                    withTagUpdate()
                }
            }
        }
    }

    fun deleteTagList(tagIdList: TagIdList) {  // 태그 삭제 기능
        viewModelScope.launch {
            tagRepository.deleteTagList(tagIdList = tagIdList) {
                if (it != null) {
                    getTag()
                    withTagUpdate()
                }
            }
        }
    }

    fun updateTag(tagId: String, updateTag: TagUpdate) {  // 태그 수정 기능
        viewModelScope.launch {
            tagRepository.updateTag(tagId = tagId, updateTag = updateTag) {
                if (it != null) {
                    getTag()
                    withTagUpdate()
                }
            }
        }
    }

    //    fun createTagList(contents : ContentList){  태그 여러개 추가
//        viewModelScope.launch {
//            tagRepository.createTagList(contents = contents){
//                getTag()
//                withTagUpdate()
//            }
//        }
//    }

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
                if (it != null) {
                    todoList.clear()
                    _todoByTag.postValue(false)
                    todoByTagItem = null

                    if (it.flaggedTodos.isNotEmpty())
                        _flaggedTodos.postValue(
                            listOf(Todo(type = 4, content = "중요"))
                                    + it.flaggedTodos + listOf(Todo(type = 3))
                        )
                    else _flaggedTodos.postValue(
                        listOf(
                            Todo(type = 4, content = "중요"),
                            Todo(type = 5, content = "중요한 할 일이 있나요?"), Todo(type = 3)
                        )
                    )

                    if (it.taggedTodos.isNotEmpty())
                        _taggedTodos.postValue(
                            listOf(
                                Todo(
                                    type = 4,
                                    content = "분류"
                                )
                            ) + it.taggedTodos + listOf(Todo(type = 3))
                        ) else _taggedTodos.postValue(
                        listOf(
                            Todo(type = 4, content = "분류"),
                            Todo(type = 5, content = "모든 할 일을 마쳤습니다!"),
                            Todo(type = 3)
                        )
                    )
                    if (it.untaggedTodos.isNotEmpty())
                        _untaggedTodos.postValue(
                            listOf(
                                Todo(
                                    type = 4,
                                    content = "미분류"
                                )
                            ) + it.untaggedTodos + listOf(Todo(type = 3))
                        ) else _untaggedTodos.postValue(
                        listOf(
                            Todo(type = 4, content = "미분류"),
                            Todo(type = 5, content = "모든 할 일을 마쳤습니다!"),
                            Todo(type = 3)
                        )
                    )
                    if (it.completedTodos.isNotEmpty())
                        _completedTodos.postValue(
                            listOf(
                                Todo(
                                    type = 4,
                                    content = "완료"
                                )
                            ) + it.completedTodos + listOf(Todo(type = 6))
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
                    Log.d("20191627", "CheckListViewModel -> GetTodoMain Fail")
                }
                callback()
            }
        }
    }

    fun getTodayTodo(
        frontEndDate: FrontEndDate,
        callback: () -> Unit
    ) { // Today 창에서 보여줄 Todo를 가져오는 기능
        viewModelScope.launch {
            todoRepository.getTodayTodo(frontEndDate = frontEndDate) {
                if (it != null) {
                    todayList.clear()
                    todayList.apply {

                        this.add(Todo(type = 4, content = "중요"))
                        if (it.flaggedTodos.isNotEmpty())
                            this.addAll(it.flaggedTodos)
                        else this.add(Todo(type = 5, content = "중요한 할 일이 있나요?"))

                        this.add(Todo(type = 3))

                        this.add(Todo(type = 4, content = "오늘 할 일"))
                        if (it.todayTodos.isNotEmpty())
                            this.addAll(it.todayTodos)
                        else this.add(Todo(type = 5, content = "모든 할 일을 마쳤습니다!"))

                        this.add(Todo(type = 3))

                        this.add(Todo(type = 4, content = "오늘 마감"))
                        if (it.endDatedTodos.isNotEmpty())
                            this.addAll(it.endDatedTodos)
                        else this.add(Todo(type = 5, content = "모든 할 일을 마쳤습니다!"))

                        this.add(Todo(type = 3))

                        this.add(Todo(type = 4, content = "완료"))
                        if (it.completedTodos.isNotEmpty())
                            this.addAll(it.completedTodos)
                        else this.add(Todo(type = 5, content = "할일을 완료해 보세요!"))
                    }
                    _todayTodo.postValue(todayList)
                } else {
                    Log.d("20191627", "CheckListViewModel -> GetTodayTdo Fail")
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
                getTag()
                getTodoMain {
                    if (it == null)
                        Log.d("20191627", "Todo 생성 실패")
                    else {
                        flaggedTodos.value?.let { todoList.addAll(it) }
                        taggedTodos.value?.let { todoList.addAll(it) }
                        untaggedTodos.value?.let { todoList.addAll(it) }
                        completedTodos.value?.let { todoList.addAll(it) }
                        _todoDataList.postValue(todoList)
                        _addTodoId.postValue(it.id)
                    }
                    callback()
                }
            }
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
                            if (it != null)
                                this.addAll(listOf(Todo(type = 4, content = todoByTagItem!!)) + it)
                        }
                    }
                    2 -> {
                        todoRepository.getTodoByUntag {
                            if (it != null)
                                this.addAll(listOf(Todo(type = 4, content = todoByTagItem!!)) + it)
                        }
                    }
                    else -> {
                        todoRepository.getTodoByTag(tagDataList.value!![position - 1].id) {
                            if (it != null) {
                                this.add(Todo(type = 4, content = "중요"))
                                if (it.flaggedTodos.isNotEmpty())
                                    this.addAll(it.flaggedTodos)
                                else this.add(Todo(type = 5, content = "중요한 할 일이 있나요?"))
                                this.add(Todo(type = 3))

                                this.add(Todo(type = 4, content = todoByTagItem!!))
                                if (it.unFlaggedTodos.isNotEmpty())
                                    this.addAll(it.unFlaggedTodos)
                                else this.add(Todo(type = 5, content = "모든 할 일을 마쳤습니다!"))
                                this.add(Todo(type = 3))

                                this.add(Todo(type = 4, content = "완료"))
                                if (it.completedTodos.isNotEmpty())
                                    this.addAll(it.completedTodos)
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
                    if (it != null)
                        addAll(listOf(Todo(type = 4, content = "중요")) + it)
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
                if (it != null) {
                    getTag()
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.d("20191627", "CheckListViewModel -> UpdateTodo Fail")
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
                if (it != null) {
                    getTag()
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.d("20191627", "CheckListViewModel -> UpdateRepeatFrontTodo Fail")
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
                if (it != null) {
                    getTag()
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.d("20191627", "CheckListViewModel -> UpdateRepeatMiddleTodo Fail")
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
                if (it != null) {
                    getTag()
                    checkTodayMode()
                    withTagUpdate()
                } else {
                    Log.d("20191627", "CheckListViewModel -> UpdateRepeatBackTodo Fail")
                }
                callback()
            }
        }
    }
    /* -------------------------------------------------------------------------------- */

    /* ----------------------------------할 일 삭제 기능---------------------------------- */
    // 반복하지 않는 Todo, 반복하는 Todo의 전체를 삭제하는 기능
    fun deleteTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val successData = todoRepository.deleteTodo(userId = userId, todoId = todoId) {
                if (it != null) {
                    if (it.success) {
                        checkTodayMode()
                        withTagUpdate()
                    }
                    callback()
                }
            }
        }
    }

    // 반복하는 할일의 front를 삭제하는 기능
    fun deleteRepeatFrontTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        frontEndDate: FrontEndDate,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val successData = todoRepository.deleteRepeatFrontTodo(userId, todoId, frontEndDate) {
                if (it != null) {
                    if (it.success) {
                        checkTodayMode()
                        withTagUpdate()
                    }
                    callback()
                }
            }
        }
    }

    fun deleteRepeatMiddleTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        middleEndDate: MiddleEndDate,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val successData = todoRepository.deleteRepeatMiddleTodo(userId, todoId, middleEndDate) {
                if (it != null) {
                    if (it.success) {
                        checkTodayMode()
                        withTagUpdate()
                    }
                    callback()
                }
            }
        }
    }

    fun deleteRepeatBackTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        backRepeatEnd: BackRepeatEnd,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val successData = todoRepository.deleteRepeatBackTodo(userId, todoId, backRepeatEnd) {
                if (it != null) {
                    if (it.success) {
                        checkTodayMode()
                        withTagUpdate()
                    }
                    callback()
                }
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
                if (it != null) {
                    if (it.success) {
                        checkTodayMode()
                        withTagUpdate()
                    }
                }
            }
        }
    }

    // 반복하지 않는 할 일, 반복하는 할 일의 전체를 완료하는 기능
    fun completeNotRepeatTodo(
        completed: Completed,
        id: String,
        callback: (completed: Completed) -> Unit
    ) {
        viewModelScope.launch {
            val successData = todoRepository.completeNotRepeatTodo(
                todoId = id,
                completed = completed
            ) {
                if (it != null) {
                    if (it.success) {
                        checkTodayMode()
                        withTagUpdate()
                    } else {
                        callback(completed)
                    }
                } else {
                    callback(completed)
                }
            }
        }
    }

    // 반복하는 할 일의 front를 완료하는 기능
    fun completeRepeatFrontTodo(id: String, frontEndDate: FrontEndDate, callback: () -> Unit) {
        viewModelScope.launch {
            val successData =
                todoRepository.completeRepeatFrontTodo(todoId = id, frontEndDate = frontEndDate) {
                    if (it != null) {
                        if (it.success) {
                            checkTodayMode()
                            withTagUpdate()
                        } else {
                            callback()
                        }
                    } else {
                        callback()
                    }
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
                if (it != null) {
                    if (it.success) {
                        checkTodayMode()
                        withTagUpdate()
                    }
                }
            }
        }
    }

    // Todo의 중요를 업데이트 하는 기능
    fun updateFlag(flag: Flag, id: String, callback: (flag: Flag) -> Unit) {
        viewModelScope.launch {
            val successData =
                todoRepository.updateFlag(todoId = id, flag = flag) {
                    if (it != null) {
                        if (it.success) {
                            checkTodayMode()
                            withTagUpdate()
                        } else {
                            callback(flag)
                        }
                    } else {
                        callback(flag)
                    }
                }
        }
    }

    fun updateOrderMainTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        changeOrderTodo: ChangeOrderTodo
    ) {
        Log.d("20191627", changeOrderTodo.todoIds.toString())
        viewModelScope.launch {
            val successData = todoRepository.updateOrderMainTodo(userId, changeOrderTodo)
            // successData가 null이면 실패임
        }
    }
}
