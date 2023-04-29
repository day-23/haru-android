package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.example.haru.data.model.*
import com.example.haru.data.repository.TagRepository
import com.example.haru.data.repository.TodoRepository
import com.example.haru.utils.FormatDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
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

    fun clearToday() {
        todayList.clear()
    }

    fun getTodoList(): List<Todo> {
        return todoList
    }

    fun getTag() {
        viewModelScope.launch {
            _tagDataList.value = basicTag + tagRepository.getTag()
        }
    }

    fun withTagUpdate() {
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

    fun getTodoMain(callback: () -> Unit) {
        viewModelScope.launch {
            todoRepository.getTodoMain {
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
                        Todo(type = 5), Todo(type = 3)
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
                        Todo(type = 5),
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
                        Todo(type = 5),
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
                        ) + it.completedTodos
                    ) else _completedTodos.postValue(
                    listOf(
                        Todo(type = 4, content = "완료"),
                        Todo(type = 5)
                    )
                )
                _todoByTag.postValue(false)
                todoByTagItem = null
            }
            callback()
        }
    }

    fun getTodayTodo(endDate: EndDate, callback: () -> Unit) {
        viewModelScope.launch {
            todoRepository.getTodayTodo(endDate = endDate) {
                todayList.clear()
                todayList.apply {
                    if (it.flaggedTodos.isNotEmpty()) {
                        this.add(Todo(type = 4, content = "중요"))
                        this.addAll(it.flaggedTodos)
                    }
                    if (it.todayTodos.isNotEmpty()) {
                        this.add(Todo(type = 4, content = "오늘 할 일"))
                        this.addAll(it.todayTodos)
                    }
                    if (it.endDatedTodos.isNotEmpty()) {
                        this.add(Todo(type = 4, content = "오늘 마감"))
                        this.addAll(it.endDatedTodos)
                    }
                    this.add(Todo(type = 3))
                    if (it.completedTodos.isNotEmpty()) {
                        this.add(Todo(type = 4, content = "완료"))
                        this.addAll(it.completedTodos)
                    }
                }
                _todayTodo.postValue(todayList)
                callback()
            }
        }
    }


    fun addTodo(todoRequest: TodoRequest, callback: () -> Unit) {
        viewModelScope.launch {
            todoRepository.createTodo(todoRequest) {
                getTag()
                getTodoMain {
                    flaggedTodos.value?.let { todoList.addAll(it) }
                    taggedTodos.value?.let { todoList.addAll(it) }
                    untaggedTodos.value?.let { todoList.addAll(it) }
                    completedTodos.value?.let { todoList.addAll(it) }
                    _todoDataList.postValue(todoList)
                    _addTodoId.postValue(it.id)
                    callback()
                }
            }
        }
    }

    fun getTodoByTag(position: Int) { // 변경사항 적용하기!!!!!!!!!!!!!!!!!
        viewModelScope.launch {
            todoByTagItem = tagDataList.value!![position - 1].content
            _todoByTag.value = true
            todoList.apply {
                clear()
                when (position) {
                    1 -> addAll(
                        listOf(
                            Todo(
                                type = 4,
                                content = todoByTagItem!!
                            )
                        ) + todoRepository.getTodoByComplete()
                    )
                    2 -> addAll(
                        listOf(
                            Todo(
                                type = 4,
                                content = todoByTagItem!!
                            )
                        ) + todoRepository.getTodoByUntag()
                    )
                    else -> {
                        todoRepository.getTodoByTag(tagDataList.value!![position - 1].id) {
                            if (it.flaggedTodos.isNotEmpty()) {
                                this.add(Todo(type = 4, content = "중요"))
                                this.addAll(it.flaggedTodos)
                                this.add(Todo(type = 3))
                            }

                            this.add(Todo(type = 4, content = todoByTagItem!!))
                            if (it.unFlaggedTodos.isNotEmpty()) {
                                this.addAll(it.unFlaggedTodos)
                                this.add(Todo(type = 3))
                            } else this.addAll(listOf(Todo(type = 5), Todo(type = 3)))

                            if (it.completedTodos.isNotEmpty()) {
                                this.add(Todo(type = 4, content = "완료"))
                                this.addAll(it.completedTodos)
                            }
                        }
                    }
                }
            }
            _todoDataList.value = todoList
        }
    }

    fun getTodoByFlag() {
        viewModelScope.launch {
            todoByTagItem = "중요"
            _todoByTag.value = true
            todoList.apply {
                clear()
                addAll(listOf(Todo(type = 4, content = "중요")) + todoRepository.getTodoByFlag())
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

    fun putTodo(todoId: String, todo: UpdateTodo, callback: () -> Unit) {
        viewModelScope.launch {
            val updateTodo = todoRepository.putTodo(todoId = todoId, todo = todo) {
                getTag()
                if (todayList.isNotEmpty()) {
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 23) // 시간을 23시로 설정
                        set(Calendar.MINUTE, 59) // 분을 59분으로 설정
                        set(Calendar.SECOND, 59) // 초를 59초로 설정
                    }
                    val todayEndDate = EndDate(FormatDate.dateToStr(calendar.time))
                    getTodayTodo(todayEndDate) {}
                }
                withTagUpdate()
                callback()
            }
        }
    }

    fun deleteTodo(todoId: String, callback: () -> Unit) {
        viewModelScope.launch {
            val successData = todoRepository.deleteTodo(todoId = todoId) {
                if (it.success) {
                    if (todayList.isNotEmpty()) {
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 23) // 시간을 23시로 설정
                            set(Calendar.MINUTE, 59) // 분을 59분으로 설정
                            set(Calendar.SECOND, 59) // 초를 59초로 설정
                        }
                        val todayEndDate = EndDate(FormatDate.dateToStr(calendar.time))
                        getTodayTodo(todayEndDate) {}
                    }
                    withTagUpdate()
                }
                callback()
            }
        }
    }

    fun updateFlag(flag: Flag, id: String) {
        viewModelScope.launch {
            val successData =
                todoRepository.updateFlag(todoId = id, flag = flag) {
                    if (it.success) {
                        if (todayList.isNotEmpty()) {
                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 23) // 시간을 23시로 설정
                                set(Calendar.MINUTE, 59) // 분을 59분으로 설정
                                set(Calendar.SECOND, 59) // 초를 59초로 설정
                            }
                            val todayEndDate = EndDate(FormatDate.dateToStr(calendar.time))
                            getTodayTodo(todayEndDate) {}
                        }
                        withTagUpdate()
                    }
                }
        }
    }

    fun updateNotRepeatTodo(completed: Completed, id: String) {
        viewModelScope.launch {
            val successData = todoRepository.updateNotRepeatTodo(
                todoId = id,
                completed = completed
            ) {
                if (it.success) {
                    if (todayList.isNotEmpty()) {
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 23) // 시간을 23시로 설정
                            set(Calendar.MINUTE, 59) // 분을 59분으로 설정
                            set(Calendar.SECOND, 59) // 초를 59초로 설정
                        }
                        val todayEndDate = EndDate(FormatDate.dateToStr(calendar.time))
                        getTodayTodo(todayEndDate) {}
                    }
                    withTagUpdate()
                }
            }
        }
    }

    fun updateRepeatTodo(id: String, endDate: EndDate) {
        viewModelScope.launch {
            val successData = todoRepository.updateRepeatTodo(todoId = id, endDate = endDate) {
                if (it.success) {
                    if (todayList.isNotEmpty()){
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 23) // 시간을 23시로 설정
                            set(Calendar.MINUTE, 59) // 분을 59분으로 설정
                            set(Calendar.SECOND, 59) // 초를 59초로 설정
                        }
                        val todayEndDate = EndDate(FormatDate.dateToStr(calendar.time))
                        getTodayTodo(todayEndDate) {}
                    }
                    withTagUpdate()
                }
            }
        }
    }

    fun updateSubTodo(completed: Completed, id: String, subTodoId: String, subTodoPosition: Int) {
        viewModelScope.launch {
            val successData = todoRepository.updateSubTodo(
                subTodoId = subTodoId,
                completed = completed
            ) {
                if (it.success) {
                    if (todayList.isNotEmpty()) {
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 23) // 시간을 23시로 설정
                            set(Calendar.MINUTE, 59) // 분을 59분으로 설정
                            set(Calendar.SECOND, 59) // 초를 59초로 설정
                        }
                        val todayEndDate = EndDate(FormatDate.dateToStr(calendar.time))
                        getTodayTodo(todayEndDate) {}
                    }
                    withTagUpdate()
                }
            }

        }
    }

    fun updateFolded(folded: Folded, id: String) {
        viewModelScope.launch {
            val successData = todoRepository.updateFolded(
                todoId = id,
                folded = folded
            ) {
                if (it.success) {
                    if (todayList.isNotEmpty()) {
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 23) // 시간을 23시로 설정
                            set(Calendar.MINUTE, 59) // 분을 59분으로 설정
                            set(Calendar.SECOND, 59) // 초를 59초로 설정
                        }
                        val todayEndDate = EndDate(FormatDate.dateToStr(calendar.time))
                        getTodayTodo(todayEndDate) {}
                    }
                    withTagUpdate()
                }
            }
        }
    }
}
