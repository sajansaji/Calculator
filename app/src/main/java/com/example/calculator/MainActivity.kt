package com.example.calculator


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
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
        hideKeyboard(binding.calc)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()

        binding.calc.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                hideKeyboard(v)
                binding.calc.requestFocus()
                updateCursorPosition(event.x)
            }
            true
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateCursorPosition(x: Float) {
        val layout = binding.calc.layout
        if (layout != null) {
            val line = layout.getLineForVertical(binding.calc.height / 2) // Use mid-point of EditText
            val offset = binding.calc.getOffsetForPosition(x, layout.getPrimaryHorizontal(line))
            binding.calc.setSelection(offset)
        }
    }

    private fun setupButtonListeners() {
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
                binding.operator.text = "×"
            }

            buttonText == "÷" -> {
                handleOperator("/")
                binding.operator.text = "÷"
            }

            buttonText.matches(Regex("[+\\-*/]")) -> {
                handleOperator(buttonText)
                binding.operator.text = buttonText
            }

            buttonText == "=" -> {
                if (currentNumber.isNotEmpty() && currentOperator.isNotEmpty()) {
                    val result = evaluateExpression(firstNumber, currentNumber, currentOperator)
                    updateInputDisplay("$firstNumber $currentOperator $currentNumber")
                    updateResultDisplay(result)
                    firstNumber = result
                    currentNumber = ""
                    currentOperator = ""
                    binding.operator.text = ""
                }
                if (currentNumber.isEmpty()){
                    updateResultDisplay(firstNumber)
                }
            }

            buttonText == "•" -> {
                handleDecimal()
            }

            buttonText == "%" -> {
                handlePercentage()
            }

            buttonText == "!" -> {
                handleFactorial()
            }
        }
    }

    private fun handlePercentage() {
        if (firstNumber.isNotEmpty()) {
            Log.e("TAG", "hanndle persontage")
            val percentageValue = firstNumber.toDoubleOrNull()
            if (percentageValue != null) {
                val result = percentageValue / 100
                updateResultDisplay(result.toString())
                currentNumber = result.toString()
                updateInputDisplay(currentNumber)
            } else {
                updateResultDisplay("hello")
                Log.e("Calculator", "Invalid input for percentage: $currentNumber")
            }
        } else {
            updateResultDisplay("Error: Input is empty")
            Log.e("Calculator", "Input is empty")
        }
    }

    private fun handleFactorial() {
        if (firstNumber.isNotEmpty()) {
            val num = firstNumber.toIntOrNull()
            updateInputDisplay("$num!")
            if (num != null && num >= 0) {
                val result = factorial(num)
                updateResultDisplay(result.toString())
                currentNumber = result.toString()
                updateResultDisplay(currentNumber)
            } else {
                updateResultDisplay("Error")
            }
        }
    }

    private fun factorial(n: Int): Int {
        return if (n <= 1) 1 else n * factorial(n - 1)
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
        // Check if we're working on the first number (before any operator)
        if (currentOperator.isEmpty()) {
            // Check if firstNumber already contains a decimal point
            if (!firstNumber.contains(".")) {
                // If firstNumber is empty, start with "0."
                // Otherwise, just add "."
                firstNumber += if (firstNumber.isEmpty()) "0." else "."
                updateInputDisplay(firstNumber) // Update display for first number
            }
        } else { // We are working on the current number (after an operator)
            // Check if currentNumber already contains a decimal point
            if (!currentNumber.contains(".")) {
                // If currentNumber is empty, start with "0."
                // Otherwise, just add "."
                currentNumber += if (currentNumber.isEmpty()) "0." else "."
                updateInputDisplay(currentNumber) // Update display for current number
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
        binding.operator.text = ""
        updateInputDisplay("0")
        updateResultDisplay("0")
    }

    private fun updateInputDisplay(value: String) {
        binding.calc.setText(value)

    }

    @SuppressLint("SetTextI18n")
    private fun updateResultDisplay(value: String) {

        try {
            // Convert the string to a Double
            val doubleValue = value.toDouble()

            // Check if the number is an integer
            if (doubleValue % 1 == 0.0) {
                // Remove the decimal part by converting to Int
                binding.res.text = doubleValue.toInt().toString()
            } else {
                // Display the original value
                binding.res.text = value
            }
        } catch (e: NumberFormatException) {
            binding.res.text = "Error"
        }
    }

    private fun evaluateExpression(
        firstNum: String,
        secondNum: String,
        operator: String
    ): String {
        val num1 = firstNum.toDoubleOrNull() ?: 0.0
        val num2 = secondNum.toDoubleOrNull() ?: 0.0

        return when (operator) {
            "+" -> (num1 + num2).toString()
            "-" -> (num1 - num2).toString()
            "*" -> (num1 * num2).toString()
            "/" -> if (num2 != 0.0) (num1 / num2).toString() else "Error"
            else -> ""
        }
    }
}