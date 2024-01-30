package fr.mathysg.testapp.domain

import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.MassType

const val METER_TO_PIXEL = 500.0

class Ball(x: Double, y: Double, val radius: Double) : Body() {

    init {
        addFixture(Geometry.createCircle(radius))
        translate(x / METER_TO_PIXEL, y / METER_TO_PIXEL)
        setMass(MassType.NORMAL)
    }



}
