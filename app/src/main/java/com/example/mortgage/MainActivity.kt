package com.example.mortgage

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * 房贷计算器主页面
 * 作者：葛昭阳  学号：2406100109  班级：计算机241班
 *
 * 功能：输入贷款金额、年利率、选择年限和还款方式，
 *      计算月供后通过 Intent 传递数据到 ResultActivity。
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取控件引用
        val etLoanAmount = findViewById<EditText>(R.id.etLoanAmount)
        val etInterestRate = findViewById<EditText>(R.id.etInterestRate)
        val spinnerTerm = findViewById<Spinner>(R.id.spinnerTerm)
        val rgPaymentMethod = findViewById<RadioGroup>(R.id.rgPaymentMethod)
        val cbIncludeInsurance = findViewById<CheckBox>(R.id.cbIncludeInsurance)
        val cbShowDetail = findViewById<CheckBox>(R.id.cbShowDetail)
        val ibInfo = findViewById<ImageButton>(R.id.ibInfo)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)

        // ImageButton：弹出说明对话框
        ibInfo.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("计算说明")
                .setMessage("等额本息：每月还款额固定，前期利息占比高。\n\n等额本金：每月偿还相同本金，利息逐月递减，总利息更少。")
                .setPositiveButton("知道了", null)
                .show()
        }

        // 计算按钮点击事件
        btnCalculate.setOnClickListener {
            val amountStr = etLoanAmount.text.toString().trim()
            val rateStr = etInterestRate.text.toString().trim()

            // 输入校验
            if (amountStr.isEmpty()) {
                etLoanAmount.error = "请输入贷款金额"
                return@setOnClickListener
            }
            if (rateStr.isEmpty()) {
                etInterestRate.error = "请输入年利率"
                return@setOnClickListener
            }

            val loanAmount = amountStr.toDoubleOrNull()
            val annualRate = rateStr.toDoubleOrNull()

            if (loanAmount == null || loanAmount <= 0) {
                etLoanAmount.error = "金额必须大于 0"
                return@setOnClickListener
            }
            if (annualRate == null || annualRate < 0 || annualRate > 30) {
                etInterestRate.error = "利率应在 0-30 之间"
                return@setOnClickListener
            }

            // 解析年限 Spinner 选中值（如 "20 年" -> 20）
            val termStr = spinnerTerm.selectedItem.toString()
            val years = termStr.replace(" 年", "").toInt()

            // 判断还款方式
            val isEqualPayment = rgPaymentMethod.checkedRadioButtonId == R.id.rbEqualPayment
            val methodName = if (isEqualPayment) "等额本息" else "等额本金"

            // 计算月供
            val months = years * 12
            val monthlyRate = annualRate / 100.0 / 12.0
            val loanInYuan = loanAmount * 10000.0  // 万元转元

            val monthlyPayment: Double
            val totalPayment: Double

            if (isEqualPayment) {
                // 等额本息公式：月供 = P × r × (1+r)^n / ((1+r)^n - 1)
                monthlyPayment = if (monthlyRate == 0.0) {
                    loanInYuan / months
                } else {
                    val factor = Math.pow(1 + monthlyRate, months.toDouble())
                    loanInYuan * monthlyRate * factor / (factor - 1)
                }
                totalPayment = monthlyPayment * months
            } else {
                // 等额本金：首月最多，逐月递减，这里取首月月供做展示
                val monthlyPrincipal = loanInYuan / months
                monthlyPayment = monthlyPrincipal + loanInYuan * monthlyRate
                // 总利息 = (n+1) × P × r / 2
                totalPayment = loanInYuan + (months + 1) * loanInYuan * monthlyRate / 2
            }

            val totalInterest = totalPayment - loanInYuan

            // 保费（如果勾选）
            val insuranceMonthly = if (cbIncludeInsurance.isChecked) loanInYuan * 0.0005 else 0.0

            // ===== Intent 传值跳转到结果页 =====
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("monthlyPayment", monthlyPayment)
                putExtra("totalInterest", totalInterest)
                putExtra("totalPayment", totalPayment)
                putExtra("loanAmount", loanInYuan)
                putExtra("years", years)
                putExtra("method", methodName)
                putExtra("monthlyRate", monthlyRate)
                putExtra("showDetail", cbShowDetail.isChecked)
                putExtra("insuranceMonthly", insuranceMonthly)
                putExtra("isEqualPayment", isEqualPayment)
            }
            startActivity(intent)
        }
    }
}
