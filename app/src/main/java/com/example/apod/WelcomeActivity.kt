package com.example.apod

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.apod.databinding.ActivityWelcomeBinding
import android.text.Html
import android.view.View
import android.widget.TextView

import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import android.content.Intent
import androidx.preference.PreferenceManager

class WelcomeActivity : AppCompatActivity() {
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var dots: Array<TextView?>
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Перед тем как настроить биндинг и contentView, пытаемся проверить,
        // запускается ли наше приложение в первый раз.
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstTimeLaunch = sharedPreferences.getBoolean("isFirstTimeLaunch", true)
        if (!isFirstTimeLaunch) launchHomeScreen()

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ViewPagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = adapter

        addBottomDots(0)

        binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener)

        binding.buttonForward.setOnClickListener{
            launchHomeScreen()
        }
    }

    /**
     * Добавляет ряд точек внизу для текущей страницы
     * @param currentPage Номер текущей страницы
     */
    private fun addBottomDots(currentPage: Int) {
        // Ряд точек представлен текстовой строкой, содержащей указанное
        // количество спецсимволов HTML в виде кружочка ("•").
        dots = arrayOfNulls(adapter.count)

        // Удаляем все элементы View из ViewGroup
        binding.layoutDots.removeAllViews()

        // Обходим массив точек, где теперь представляем каждую точку в виде
        // отдельного элемента TextView.
        // Устанавливаем ей текст (вышеупомянутый спецсимвол), размер и цвет
        // шрифта, после чего добавляем этот TextView с точкой в
        // горизонтальный LinearLayout
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(resources.getColor(R.color.dot_dark))
            binding.layoutDots.addView(dots[i])
        }

        // Если длина массива больше нуля, устанавливаем точке текущей
        // страницы активный цвет
        if (dots.isNotEmpty()) dots[currentPage]?.setTextColor(resources.getColor(R.color.dot_light))
    }

    private fun launchHomeScreen() {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)

        sharedPreferences.edit().putBoolean("isFirstTimeLaunch", false).apply()

        startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
        finish()
    }

    private var viewPagerPageChangeListener: OnPageChangeListener =
        object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                addBottomDots(position)

                // Если это последняя страница, отображаем кнопку перехода на
                // главный экран
                if (position == dots.size - 1) {
                    binding.buttonForward.visibility = View.VISIBLE
                } else {
                    binding.buttonForward.visibility = View.INVISIBLE
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        }
}