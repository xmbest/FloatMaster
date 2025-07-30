package com.xmbest.floatmaster.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.xmbest.floatmaster.R
import com.xmbest.floatmaster.ui.screen.home.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val tabs = listOf(
        NavigationItem(
            title = stringResource(R.string.nav_home),
            icon = Icons.Default.Home,
            screen = { HomeScreen() }
        ),
        NavigationItem(
            title = stringResource(R.string.nav_about),
            icon = Icons.Default.Info,
            screen = { AboutScreen() }
        )
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        // 显示当前选中的页面，传递paddingValues以避免被底部导航栏遮挡
        Box(modifier = Modifier.padding(paddingValues)) {
            tabs[selectedTab].screen()
        }
    }
}

data class NavigationItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val screen: @Composable () -> Unit
)