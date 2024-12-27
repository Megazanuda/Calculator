package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvInput = findViewById<TextView>(R.id.tvInput)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                val text = (it as Button).text.toString()
                tvInput.append(text)
            }
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            tvInput.text = ""
            tvResult.text = ""
        }

        findViewById<Button>(R.id.btnResult).setOnClickListener {
            val expression = tvInput.text.toString()
            try {
                val result = evaluateExpression(expression)
                tvResult.text = result.toString()
            } catch (e: Exception) {
                tvResult.text = "Ошибка"
            }
        }
    }

    /**
     * Функция для вычисления выражения, поддерживающего +, -, *, /.
     */
    private fun evaluateExpression(expression: String): Double {
        val tokens = tokenize(expression)
        val values = Stack<Double>()
        val operators = Stack<Char>()

        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]

            when {
                token.isDouble() -> {
                    values.push(token.toDouble())
                }
                token.isOperator() -> {
                    while (operators.isNotEmpty() && hasPrecedence(token[0], operators.peek())) {
                        values.push(applyOperation(operators.pop(), values.pop(), values.pop()))
                    }
                    operators.push(token[0])
                }
                else -> throw IllegalArgumentException("Некорректное выражение")
            }
            i++
        }

        while (operators.isNotEmpty()) {
            values.push(applyOperation(operators.pop(), values.pop(), values.pop()))
        }

        return if (values.size == 1) values.pop() else throw IllegalArgumentException("Ошибка при вычислении")
    }

    /**
     * Преобразование строки в список токенов (чисел и операторов).
     */
    private fun tokenize(expression: String): List<String> {
        val regex = Regex("([0-9]+\\.?[0-9]*|[+\\-*/])")
        return regex.findAll(expression).map { it.value }.toList()
    }

    /**
     * Проверяет приоритет операторов.
     */
    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false
        }
        return true
    }

    /**
     * Применяет оператор к двум операндам.
     */
    private fun applyOperation(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> {
                if (b == 0.0) throw ArithmeticException("Деление на ноль")
                a / b
            }
            else -> throw IllegalArgumentException("Неизвестный оператор: $op")
        }
    }

    /**
     * Проверяет, является ли строка числом.
     */
    private fun String.isDouble(): Boolean {
        return this.toDoubleOrNull() != null
    }

    /**
     * Проверяет, является ли строка оператором.
     */
    private fun String.isOperator(): Boolean {
        return this in listOf("+", "-", "*", "/")
    }
}
