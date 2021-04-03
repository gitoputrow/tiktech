package com.example.tiktech

open class Login(private var User: String, private var Pass: String, private var UserInput: String, private var PassInput: String) : Data {
    private var Status : Boolean
    init {
        this.UserInput = UserInput
        this.User = User
        this.PassInput = PassInput
        this.Pass = Pass
        this.Status = false
        setStatus()
    }
    override fun getUser(): String { return this.User}
    override fun setUser(User: String) { this.User = User}
    override fun getPass(): String { return this.Pass }
    override fun setPass(Pass: String) { this.Pass = Pass }
    fun getUserInput(): String { return this.UserInput }
    fun setUserInput(UserInput: String){this.UserInput = UserInput}
    fun getPassInput(): String { return this.PassInput }
    fun setPassInput(PassInput: String){this.PassInput = PassInput}
    fun getStatus(): Boolean { return this.Status }
    fun setStatus(){
        this.Status = (User.equals(UserInput)) and (Pass.equals(PassInput))
    }
}