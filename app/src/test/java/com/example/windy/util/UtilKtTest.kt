package com.example.windy.util

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class UtilKtTest {

    @Before
    fun setup() {
    }

    @Test
    fun dateFormat_returnCorrectDateFormat() {
        assertThat(dateFormat(164560539)).isEqualTo("20March1975")
    }

    @Test
    fun timeFormat_returnCorrectTimeFormat() {
        assertThat(timeFormat(164560539)).isEqualTo("05:00 PM")
    }
}