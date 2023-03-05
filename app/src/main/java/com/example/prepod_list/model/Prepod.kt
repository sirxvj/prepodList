package com.example.prepod_list.model

typealias prepListen=(List<PrepodItem>)->Unit
class Prepod : ArrayList<PrepodItem>(){
    private val listeners = mutableListOf<prepListen>()
}