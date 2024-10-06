package com.example.calculator


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Binding variable for view binding
    private lateinit var binding: ActivityMainBinding

    // Variables to hold numbers and operator
    private var firstNumber = ""
    private var currentNumber = ""
    private var currentOperator = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set flags for no limit screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Initialize views and set button click listeners
        setupButtonListeners()

        binding.ac.setOnClickListener {
            clearAll()
        }

        binding.bk.setOnClickListener {
            handleBackspace()
        }
    }

    private fun setupButtonListeners() {
        // Get all buttons from the layout and set click listeners
        val buttons = listOf<Button>(
            binding.bt0, binding.bt1, binding.bt2,
            binding.bt3, binding.bt4, binding.bt5,
            binding.bt6, binding.bt7, binding.bt8,
            binding.bt9, binding.add, binding.difference,
            binding.multiply, binding.division,
            binding.equal, binding.ac,
            binding.dot, binding.percent, binding.fact
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                val buttonText = button.text.toString()
                handleButtonClick(buttonText)
            }
        }
    }

    private fun handleButtonClick(buttonText: String) {
        when {
            buttonText.matches(Regex("[0-9]")) -> {
                if (currentOperator.isEmpty()) {
                    firstNumber += buttonText
                    updateInputDisplay(firstNumber)
                } else {
                    currentNumber += buttonText
                    updateInputDisplay(currentNumber)
                }
            }

            buttonText == "×" -> {
                handleOperator("*")
            }

            buttonText == "÷" -> {
                handleOperator("/")
            }

            buttonText.matches(Regex("[+\\-*/]")) -> {
                handleOperator(buttonText)
            }

            buttonText == "=" -> {
                if (currentNumber.isNotEmpty() && currentOperator.isNotEmpty()) {
                    val result = evaluateExpression(firstNumber, currentNumber, currentOperator)
                    updateInputDisplay("$firstNumber $currentOperator $currentNumber")
                    updateResultDisplay(result)
                    firstNumber = result
                    currentNumber = ""
                    currentOperator = ""
                }
            }

            buttonText == "." -> {
                handleDecimal()
            }
        }
    }

    private fun handleOperator(operator: String) {
        if (firstNumber.isNotEmpty()) {
            if (currentNumber.isNotEmpty()) {
                val result = evaluateExpression(firstNumber, currentNumber, currentOperator)
                updateInputDisplay(result)
                firstNumber = result
                currentNumber = ""
            }
            currentOperator = operator
            Log.e("Calculator", "Operator set: $currentOperator")
        }
    }

    private fun handleDecimal() {
        if (currentOperator.isEmpty()) {
            if (!firstNumber.contains(".")) {
                firstNumber += if (firstNumber.isEmpty()) "0." else "."
                updateInputDisplay(firstNumber)
            }
        } else {
            if (!currentNumber.contains(".")) {
                currentNumber += if (currentNumber.isEmpty()) "0." else "."
                updateInputDisplay(currentNumber)
            }
        }
    }

    private fun handleBackspace() {
        if (currentOperator.isEmpty()) {
            if (firstNumber.isNotEmpty()) {
                firstNumber = firstNumber.dropLast(1)
                updateInputDisplay(if (firstNumber.isEmpty()) "0" else firstNumber)
            }
        } else {
            if (currentNumber.isNotEmpty()) {
                currentNumber = currentNumber.dropLast(1)
                updateInputDisplay(if (currentNumber.isEmpty()) "0" else currentNumber)
            }
        }
    }

    private fun clearAll() {
        firstNumber = ""
        currentNumber = ""
        currentOperator = ""
        updateInputDisplay("0") // Reset display to 0
        updateResultDisplay("0")
    }

    private fun updateInputDisplay(value: String) {
        Log.e("Calculator", "Updating input display with value: $value")
        binding.calc.setText(value)
    }

    private fun updateResultDisplay(value: String) {
        Log.e("Calculator", "Updating result display with value: $value")
        binding.res.text = value
    }

    private fun evaluateExpression(
        firstNum: String,
        secondNum: String,
        operator: String
    ): String {
        val num1 = firstNum.toDoubleOrNull() ?: 0.0 // Handle potential conversion errors
        val num2 = secondNum.toDoubleOrNull() ?: 0.0

        Log.e("Operation", "Evaluating: $num1 $operator $num2")

        return when (operator) {
            "+" -> (num1 + num2).toString()
            "-" -> (num1 - num2).toString()
            "*" -> (num1 * num2).toString()
            "/" -> if (num2 != 0.0) (num1 / num2).toString() else "Error" // Handle division by zero
            else -> ""
        }
    }

}