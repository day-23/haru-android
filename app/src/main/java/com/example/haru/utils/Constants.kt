package com.example.haru.utils


//API 호출 결과 enum
enum class RESPONSE_STATUS {
    OKAY, FAIL, NO_CONTENT
}

//10.0.0.2
// kmu5g 10.30.113.12
// 106 - 5g 192.168.0.48
// 10.0.2.2
object API {
//    const val BASE_URL : String = "https://jsonplaceholder.typicode.com/"
//    const val BASE_URL: String = "http://10.0.2.2:8000/api/"
    //    const val BASE_URL: String = "http://192.168.0.13:8000/api/"
    const val API_KEY: String = "3b9eb1c9-4bce-462c-a4f5-67daa4fa5574"
    const val RECENT_POSTS: String = "posts/recent/{postId}"
    const val POSTS: String = "posts"
    const val COMMENTS: String = "comments"
    const val TODOS: String = "todos"
    const val USERS: String = "users"
}
