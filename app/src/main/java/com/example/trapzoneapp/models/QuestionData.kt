package com.example.trapzoneapp.models

data class QuestionData(
    val type: String = "",
    val op1: Int = 0,
    val op2: Int = 0,
    //val result: Int = 0
) {
    companion object {
        fun generateQuestionData(q: Question): QuestionData {
            return QuestionData(
                type = q::class.simpleName ?: "",
                op1 = q.op1,
                op2 = q.op2,
               // result = q.result
            )
        }

        fun generateQuestionFromData(qdata: QuestionData): Question {
            return when (qdata.type) {
                "FourDigit" -> Question.FourDigit(qdata.op1, qdata.op2)
                "ThreeDigit" -> Question.ThreeDigit(qdata.op1, qdata.op2)
                "TwoDigit" -> Question.TwoDigit(qdata.op1, qdata.op2)
                else -> Question.OneDigit(qdata.op1, qdata.op2)
            }
        }
    }
}