package extrydev.app.yuknaklet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfo(
    @ColumnInfo(name = "token") var token: String?,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)
