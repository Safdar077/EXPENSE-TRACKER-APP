package com.sk.expencetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.sk.expencetracker.ui.theme.ExpenceTrackerTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.sp

//import androidx.compose.runtime.LaunchedEffect as LaunchedEffect

class MainActivity: ComponentActivity(){
    private lateinit var dataStore: ExpenseDataStore

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore = ExpenseDataStore(applicationContext)
        setContent {
            ExpenceTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFFFFFF) ){
                    ExpenceApp(dataStore)
                }
            }
        }
    }
}

@Composable
fun ExpenceApp(dataStore: ExpenseDataStore) {
    var Expence_Name by remember { mutableStateOf(TextFieldValue("")) }
    var Amount by remember { mutableStateOf(TextFieldValue("")) }
    var expences by remember { mutableStateOf(listOf<String>()) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        dataStore.getExpenses().collect {
            expences = it
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {
        Text("Expense Tracker", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(30.dp))

        //Expence Name input.
        OutlinedTextField(
            value = Expence_Name,
            onValueChange = { Expence_Name = it },
            label = { Text("Expence Name") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )
        Spacer(modifier = Modifier.height((16.dp)))

//Expence amount imput.
        OutlinedTextField(
            value = Amount,
            onValueChange = { Amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )
        Spacer(modifier = Modifier.height((10.dp)))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(
                onClick = {
                    if (Expence_Name.text.isNotEmpty() && Amount.text.isNotEmpty()) {
                        val newExpense = "${Expence_Name.text}: \$${Amount.text}"
                        expences = expences + newExpense
                        Expence_Name = TextFieldValue("")
                        Amount = TextFieldValue("")

                        //Save New List to Data Stores.
                        coroutineScope.launch {
                            dataStore.saveExpenses(expences)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff1976d2),
                    contentColor = Color.White
                )
            ) {
                Text("Add Expence")
            }
        }
        //All Clear Button.
        Button(
            onClick = {
                expences = listOf()
                coroutineScope.launch {
                    dataStore.saveExpenses(expences)
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xfff44336),
                contentColor = Color.White
            )
        ) {
            Text("Clear All")
        }

        Spacer(modifier = Modifier.height(24.dp))

        //List of Expenses
        Text("Expenses", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(30.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(expences) { expense ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = expense,
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                    )
                }
            }
        }
    }
}