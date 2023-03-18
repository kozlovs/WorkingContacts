package ru.kozlovss.workingcontacts.presentation.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.ActivityMainBinding
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        if (viewModel.isLogin()) {
            graph.setStartDestination(R.id.feedFragment)
        } else {
            graph.setStartDestination(R.id.startFragment)
        }

        navController.setGraph(graph, intent.extras)
        setDestinationChangedListener(navController)

        navView.setupWithNavController(navController)
    }

    private fun setDestinationChangedListener(navController: NavController) = with(binding) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
           when(destination.id) {
               R.id.feedFragment -> navView.visibility = View.VISIBLE
               R.id.eventsFragment -> navView.visibility = View.VISIBLE
               R.id.myWallFragment -> navView.visibility = View.VISIBLE
               else -> navView.visibility = View.GONE
           }
        }
    }
}