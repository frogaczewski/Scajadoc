package org.scajadoc.mthesis

object University extends Application {
    var name : String = _
    def listOfStudents() = {List(Student("Filip"))}
    println(listOfStudents())
}
