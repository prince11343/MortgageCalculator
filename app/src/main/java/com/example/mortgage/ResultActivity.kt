package com.example.mortgage

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

/**
 * 房贷计算结果页
 * 作者：葛昭阳  学号：2406100109  班级：计算机241班
 *
 * 功能：接收 MainActivity 通过 Intent 传递的计算结果，
 *      展示月供汇总，并用 ListView 展示前 12 期还款明细。
 */
class ResultActivity : AppCompatActivity() {

    private val fmt = DecimalFormat("#,##0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // ===== 接收 Intent 传值 =====
        val monthlyPayment = intent.getDoubleExtra("monthlyPayment", 0.0)
        val totalInterest = intent.getDoubleExtra("totalInterest", 0.0)
        val totalPayment = intent.getDoubleExtra("totalPayment", 0.0)
        val loanAmount = intent.getDoubleExtra("loanAmount", 0.0)
        val years = intent.getIntExtra("years", 0)
        val method = intent.getStringExtra("method") ?: ""
        val monthlyRate = intent.getDoubleExtra("monthlyRate", 0.0)
        val showDetail = intent.getBooleanExtra("showDetail", false)
        val insuranceMonthly = intent.getDoubleExtra("insuranceMonthly", 0.0)
        val isEqualPayment = intent.getBooleanExtra("isEqualPayment", true)

        // 绑定控件
        val tvMonthlyPayment = findViewById<TextView>(R.id.tvMonthlyPayment)
        val tvTotalInterest = findViewById<TextView>(R.id.tvTotalInterest)
        val tvTotalPayment = findViewById<TextView>(R.id.tvTotalPayment)
        val tvMethod = findViewById<TextView>(R.id.tvMethod)
        val tvDetailTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val lvSchedule = findViewById<ListView>(R.id.lvSchedule)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // 显示汇总信息
        val displayMonthly = monthlyPayment + insuranceMonthly
        tvMonthlyPayment.text = "月供：${fmt.format(displayMonthly)} 元"
        if (insuranceMonthly > 0) {
            tvMonthlyPayment.append("（含保费 ${fmt.format(insuranceMonthly)}）")
        }
        tvTotalInterest.text = "总利息：${fmt.format(totalInterest)} 元"
        tvTotalPayment.text = "总还款额：${fmt.format(totalPayment)} 元"
        tvMethod.text = "贷款 ${fmt.format(loanAmount)} 元 · $years 年 · $method"

        // ===== 构建 ListView 明细数据 =====
        if (showDetail) {
            tvDetailTitle.visibility = View.VISIBLE
            lvSchedule.visibility = View.VISIBLE

            val months = years * 12
            val showCount = minOf(12, months)  // 最多展示前 12 期
            val items = mutableListOf<HashMap<String, String>>()

            var remainingPrincipal = loanAmount
            val monthlyPrincipal = if (isEqualPayment) 0.0 else loanAmount / months

            for (i in 1..showCount) {
                val interestThisMonth = remainingPrincipal * monthlyRate
                val payment: Double
                val principalThisMonth: Double

                if (isEqualPayment) {
                    // 等额本息：月供固定，利息 = 剩余本金 × 月利率
                    payment = monthlyPayment
                    principalThisMonth = payment - interestThisMonth
                } else {
                    // 等额本金：本金固定，月供 = 本金 + 利息
                    principalThisMonth = monthlyPrincipal
                    payment = principalThisMonth + interestThisMonth
                }

                remainingPrincipal -= principalThisMonth

                val map = HashMap<String, String>()
                map["period"] = "第 ${i} 期"
                map["payment"] = fmt.format(payment + insuranceMonthly) + " 元"
                map["principal"] = "本金 ${fmt.format(principalThisMonth)}"
                map["interest"] = "利息 ${fmt.format(interestThisMonth)}"
                items.add(map)
            }

            // SimpleAdapter 绑定到 ListView
            val adapter = SimpleAdapter(
                this,
                items,
                R.layout.item_schedule,
                arrayOf("period", "payment", "principal", "interest"),
                intArrayOf(R.id.tvPeriod, R.id.tvPayment, R.id.tvPrincipal, R.id.tvInterest)
            )
            lvSchedule.adapter = adapter

        } else {
            // 未勾选展示明细则隐藏
            tvDetailTitle.visibility = View.GONE
            lvSchedule.visibility = View.GONE
        }

        // 返回按钮
        btnBack.setOnClickListener {
            finish()
        }
    }
}
