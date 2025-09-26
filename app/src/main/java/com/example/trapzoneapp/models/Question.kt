package com.example.trapzoneapp.models

import kotlin.random.Random

sealed class Question(val time: Int, val op1: Int, val op2: Int) {

    val result: Int = op1 + op2

    class FourDigit(op1: Int, op2: Int) : Question(17, op1, op2)
    class ThreeDigit(op1: Int, op2: Int) : Question(12, op1, op2)
    class TwoDigit(op1: Int, op2: Int) : Question(8, op1, op2)
    class OneDigit(op1: Int, op2: Int) : Question(5, op1, op2)

    companion object {
        fun generate(type: Trap): Question {
            return when (type) {
                is Trap.Hard -> FourDigit(
                    Random.nextInt(1000, 9999),
                    Random.nextInt(1000, 9999)
                )
                is Trap.Medium -> ThreeDigit(
                    Random.nextInt(100, 999),
                    Random.nextInt(100, 999)
                )
                is Trap.Easy -> TwoDigit(
                    Random.nextInt(10, 99),
                    Random.nextInt(10, 99)
                )
                is Trap.VeryEasy -> OneDigit(
                    Random.nextInt(1, 9),
                    Random.nextInt(1, 9)
                )
            }
        }
    }
}
