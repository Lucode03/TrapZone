package com.example.trapzoneapp.models

import kotlin.random.Random

sealed class Question(val time: Int,val op1:Int,val op2:Int)
{
    object FourDigit : Question(
        17,
        op1=Random.nextInt(1000, 9999),
        op2=Random.nextInt(1000, 9999)
    ){
        val result = op1 + op2
    }
    object ThreeDigit : Question(
        12,
        op1 = Random.nextInt(100, 999),
        op2 = Random.nextInt(100, 999)
    ){
        val result = op1 + op2
    }
    object TwoDigit : Question(8,
        op1 = Random.nextInt(10, 99),
        op2 = Random.nextInt(10, 99)
    ){
        val result = op1 + op2
    }
    object OneDigit : Question(5,
        op1 = Random.nextInt(1, 9),
        op2 = Random.nextInt(1, 9)
    ){
        val result = op1 + op2
    }
}