package com.app.dailydeliveryrecords

import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.dailydeliveryrecords.ui.bottomnav.*
import com.app.dailydeliveryrecords.ui.common.SimpleAlertDialog
import com.app.dailydeliveryrecords.ui.theme.DailyDeliveryRecordsTheme
import com.app.dailydeliveryrecords.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel:HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyDeliveryRecordsTheme {
                MainScreenView()
            }
        }
    }


    @Composable
    fun MainScreenView() {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Monthly,
            BottomNavItem.Receipts,
            BottomNavItem.Setting
        )

        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                BottomNavigation(backgroundColor = MaterialTheme.colors.primary) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    painterResource(id = screen.icon),
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.screen_route } == true,
                            selectedContentColor = colorResource(id = R.color.beige),
                            unselectedContentColor = Color.Gray,
                            onClick = {
                                navController.navigate(screen.screen_route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            },
            backgroundColor = colorResource(id = R.color.beige)
        ) {
            NavigationGraph(navController = navController, viewModel,it)
        }
    }


    @Composable
    fun NavigationGraph(navController: NavHostController,viewModel:HomeViewModel, paddingValues: PaddingValues) {

        val showRationaleLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
        val showRationale by showRationaleLiveData.observeAsState(initial = false)

        NavHost(
            navController,
            startDestination = BottomNavItem.Home.screen_route,
            Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.screen_route) {
                HomeScreen(viewModel)
            }
            composable(BottomNavItem.Monthly.screen_route) {
                MonthlyScreen(viewModel)
            }
            composable(BottomNavItem.Receipts.screen_route) {
                ReceiptScreen(viewModel)
            }
            composable(BottomNavItem.Setting.screen_route) {
                SettingScreen(
                    viewModel = viewModel,
                    activity = this@MainActivity,
                    showRationale = showRationale,
                    showRationaleLiveData = showRationaleLiveData
                )
            }


        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    DailyDeliveryRecordsTheme {
//        MainScreenView()
//    }
//}