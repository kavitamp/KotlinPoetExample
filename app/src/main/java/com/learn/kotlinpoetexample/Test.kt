package com.learn.kotlinpoetexample

import com.learn.annotation.GenerateGreeting

@GenerateGreeting
class Test {
}


fun main() {
    print(Test())
}
