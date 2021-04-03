package com.example.tiktech

open class Regist(private var User: String, private var Pass: String, private var Name : String, private var Email : String,var Profile : String) : Data{
    init {
        this.User = User
        this.Pass = Pass
        this.Name = Name
        this.Email = Email
        this.Profile = Profile
    }
    override fun getUser(): String { return this.User }
    override fun setUser(User: String) { this.User = User }
    override fun getPass(): String { return this.Pass }
    override fun setPass(Pass: String) { this.Pass = Pass }
    fun getName() :String {return this.Name}
    fun setName(Name: String){this.Name = Name}
    fun getEmail() :String {return this.Email}
    fun setEmail(Email: String){this.Email = Email}
    override fun toString() : String{
        return " " + Name +"\n" +
                " " + User +"\n" +
                " " + Email +"\n" +
                " " + Pass +"\n" +
                " " + Profile
    }
}