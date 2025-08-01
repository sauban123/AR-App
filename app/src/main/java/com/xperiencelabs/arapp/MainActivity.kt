package com.xperiencelabs.arapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    private lateinit var modelNode: ArModelNode
    private lateinit var modelSpinner: Spinner

    private val modelMap = mapOf(
        "3D Cube" to "rubiks_cube.glb",
        "cone" to "cone.glb",
        "car" to "car.glb",
        "apple" to "apple.glb"
    )

    private var selectedModel = "rubiks_cube.glb"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById<ArSceneView>(R.id.sceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED
        }

        modelSpinner = findViewById(R.id.modelSpinner)

        // Spinner adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modelMap.keys.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modelSpinner.adapter = adapter

        modelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedName = parent.getItemAtPosition(position).toString()
                selectedModel = modelMap[selectedName] ?: "rubiks_cube.glb"
                placeModel()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        modelNode = ArModelNode(sceneView.engine, PlacementMode.INSTANT).apply {
            onAnchorChanged = {
                // Optional: Logic when anchored
            }
        }
        sceneView.addChild(modelNode)
    }


//    private fun placeModel() {
//        modelNode.loadModelGlbAsync(
//            glbFileLocation = "models/$selectedModel",
//            scaleToUnits = 0.4f,
//            centerOrigin = Position(-0.5f)
//        ) {
//            modelNode.anchor()
//            sceneView.planeRenderer.isVisible = false
//        }
//    }
//private fun placeModel() {
//    modelNode = ArModelNode(sceneView.engine, PlacementMode.INSTANT).apply {
//        loadModelGlbAsync(
//            glbFileLocation = "models/$selectedModel",
//            scaleToUnits = 0.3f,
//        ) {
//            transform.position = Position(0f, 0f, -0.5f) // Place 0.5m in front
//            anchor()
//            sceneView.planeRenderer.isVisible = false
//        }
//    }
//
//    sceneView.addChild(modelNode)
//}

    private fun placeModel() {
        modelNode = ArModelNode(sceneView.engine, PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/$selectedModel",
                scaleToUnits = 0.4f
            ) {
                // Place the model 0.5 meters in front of the camera
                val cameraPosition = sceneView.cameraNode.transform.position
                val forward = sceneView.cameraNode.transform.forward

                // Position = camera + (forward direction * distance)
                val targetPosition = cameraPosition + forward * 0.5f
                transform.position = targetPosition

                anchor() // Anchor the model to this position
                sceneView.planeRenderer.isVisible = false
            }
        }
        sceneView.addChild(modelNode)
    }

}
