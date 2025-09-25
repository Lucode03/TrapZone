package com.example.trapzoneapp.models

sealed class Trap(val question: Question,val losingPoints:Int,val winningPoints:Int)
{
    object Hard : Trap(Question.FourDigit,50,80)
    object Medium : Trap(Question.ThreeDigit,40,60)
    object Easy : Trap(Question.TwoDigit,30,40)
    object VeryEasy : Trap(Question.OneDigit,20,30)
}