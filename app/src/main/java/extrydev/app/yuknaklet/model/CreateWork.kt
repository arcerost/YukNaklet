package extrydev.app.yuknaklet.model

import java.util.ArrayList

data class CreateWork(val fromWhere: CreateWorkFromWhere, val toWhere: CreateWorkToWhere, val fuel: Int, val kdv: Int, val price: Int, val commision: Int, val carType: String, val category: String, val description: String, val image: ArrayList<String>)
