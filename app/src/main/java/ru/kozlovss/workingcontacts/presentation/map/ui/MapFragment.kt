package ru.kozlovss.workingcontacts.presentation.map.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.databinding.FragmentMapBinding
import ru.kozlovss.workingcontacts.domain.util.StringArg
import ru.kozlovss.workingcontacts.presentation.map.viewmodel.MapViewModel
import ru.kozlovss.workingcontacts.presentation.newevent.viewmodel.NewEventViewModel
import ru.kozlovss.workingcontacts.presentation.newpost.viewmodel.NewPostViewModel

class MapFragment : Fragment() {
    private val viewModel: MapViewModel by activityViewModels()
    private val newPostViewModel: NewPostViewModel by activityViewModels()
    private val newEventViewModel: NewEventViewModel by activityViewModels()
    private var mapView: MapView? = null
    private lateinit var userLocation: UserLocationLayer
    private lateinit var binding: FragmentMapBinding
    private var listener: InputListener? = null
    private lateinit var locationObjectListener: UserLocationObjectListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.initialize(context)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        arguments?.sourcePageTag?.let { initListener() }
        initLocationObjectListener()
        initPermissionLauncher()
        initMapView()
        setListeners()
        subscribe()
        arguments?.let {
            toTargetPlace(it.lat, it.lon)
        }

        return binding.root
    }

    private fun toTargetPlace(latitude: String?, longitude: String?) = with(binding.mapview) {
        if (checkCoordinates(latitude, longitude)) {
            val cameraPosition = map.cameraPosition
            map.move(
                CameraPosition(
                    Point(latitude!!.toDouble(), longitude!!.toDouble()),
                    10F,
                    cameraPosition.azimuth,
                    cameraPosition.tilt,
                )
            )
        }
    }

    private fun checkCoordinates(latitude: String?, longitude: String?): Boolean {
        if (latitude == null || longitude == null) return false
        try {
            val latd = latitude.trim().toDouble()
            val lond = longitude.trim().toDouble()
            if (latd > Coordinates.MAX_LATITUDE || latd < Coordinates.MIN_LATITUDE) return false
            if (lond > Coordinates.MAX_LONGITUDE || lond < Coordinates.MIN_LONGITUDE) return false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun initListener() {
        listener = object : InputListener {
            override fun onMapTap(map: Map, point: Point) = Unit

            override fun onMapLongTap(map: Map, point: Point) {
                val latitude = point.latitude.toString().substring(0, 9)
                val longitude = point.longitude.toString().substring(0, 9)
                MaterialAlertDialogBuilder(requireContext())
                    .create().apply {
                        setTitle("Coordinates")
                        setMessage("latitude: $latitude\nlongitude: $longitude")
                        setIcon(R.drawable.baseline_place_24)
                        setButton(
                            AlertDialog.BUTTON_POSITIVE,
                            getString(android.R.string.ok)
                        ) { _, _ ->
                            viewModel.setPlace(
                                Coordinates(
                                    latitude,
                                    longitude
                                )
                            )
                        }
                        setButton(
                            AlertDialog.BUTTON_NEGATIVE,
                            getString(android.R.string.cancel)
                        ) { _, _ ->
                            dismiss()
                        }
                    }.show()
            }
        }
    }

    private fun initLocationObjectListener() {
        locationObjectListener = object : UserLocationObjectListener {
            override fun onObjectAdded(view: UserLocationView) = Unit

            override fun onObjectRemoved(view: UserLocationView) = Unit

            override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
                userLocation.cameraPosition()?.target?.let {
                    mapView?.map?.move(CameraPosition(it, 10F, 0F, 0F))
                }
                userLocation.setObjectListener(null)
            }
        }
    }

    private fun initPermissionLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                when {
                    granted -> {
                        userLocation.isVisible = true
                        userLocation.isHeadingEnabled = false
                        userLocation.cameraPosition()?.target?.also {
                            val map = mapView?.map ?: return@registerForActivityResult
                            val cameraPosition = map.cameraPosition
                            map.move(
                                CameraPosition(
                                    it,
                                    cameraPosition.zoom,
                                    cameraPosition.azimuth,
                                    cameraPosition.tilt,
                                )
                            )
                        }
                    }
                    else -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.need_permission),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun initMapView() {
        mapView = binding.mapview.apply {
            userLocation = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
            if (requireActivity()
                    .checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                userLocation.isVisible = true
                userLocation.isHeadingEnabled = false
            }

            listener?.let { map.addInputListener(it) }

            userLocation.setObjectListener(locationObjectListener)
        }
    }

    fun setListeners() = with(binding) {
        plus.setOnClickListener {
            mapview.map.move(
                CameraPosition(
                    mapview.map.cameraPosition.target,
                    mapview.map.cameraPosition.zoom + 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1F),
                null
            )
        }

        minus.setOnClickListener {
            mapview.map.move(
                CameraPosition(
                    mapview.map.cameraPosition.target,
                    mapview.map.cameraPosition.zoom - 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1F),
                null,
            )
        }

        location.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun subscribe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.coordinates.collect {
                    it?.let {
                        val tag = arguments?.sourcePageTag
                        tag?.let { tag ->
                            when (tag) {
                                SourcePage.NEW_EVENT.toString() -> {
                                    newEventViewModel.setCoordinates(it)
                                }
                                SourcePage.NEW_POST.toString() -> {
                                    newPostViewModel.setCoordinates(it)
                                }
                                else -> {}
                            }
                        }
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearPlace()
        mapView = null
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    companion object {
        var Bundle.sourcePageTag: String? by StringArg
        var Bundle.lat: String? by StringArg
        var Bundle.lon: String? by StringArg

        enum class SourcePage {
            NEW_EVENT, NEW_POST
        }
    }
}