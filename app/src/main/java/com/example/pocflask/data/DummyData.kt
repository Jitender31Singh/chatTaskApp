package com.example.pocflask.data

data class Customer(val id: Int, val name: String)

val dummyCustomers = listOf(
    Customer(1, "Acme Corp."),
    Customer(2, "Globex Inc."),
    Customer(3, "Initech Ltd.")
)