package fr.mathysg.testapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import fr.mathysg.testapp.domain.Ball
import fr.mathysg.testapp.domain.METER_TO_PIXEL
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.MassType
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.NarrowphaseCollisionData
import org.dyn4j.world.World
import org.dyn4j.world.listener.CollisionListener
import org.dyn4j.world.listener.CollisionListenerAdapter
import java.util.Timer
import java.util.TimerTask

class MergeEvent(val one: Ball, val two: Ball, val new: Ball)

class BallView(context: Context) : View(context) {

    private val ballPaint: Paint = Paint()
    private var world = World<Body>()
    private var merges = mutableListOf<MergeEvent>()

    init {
        ballPaint.isAntiAlias = true
        ballPaint.color = Color.RED
        ballPaint.style = Paint.Style.FILL
        ballPaint.strokeJoin = Paint.Join.ROUND
        ballPaint.strokeWidth = 4f

        world.gravity = Vector2(0.0, 9.8)

        val simWidth = getScreenWidth().toDouble() / METER_TO_PIXEL
        val simHeight = getScreenHeight().toDouble() / METER_TO_PIXEL

        val bottom = Body()
        bottom.addFixture(Geometry.createRectangle(simWidth, 0.01))
        bottom.translate(simWidth / 2, simHeight)
        bottom.setMass(MassType.INFINITE)
        world.addBody(bottom)

        val left = Body()
        left.addFixture(Geometry.createRectangle(0.01, simHeight))
        left.translate(0.0, simHeight / 2)
        left.setMass(MassType.INFINITE)
        world.addBody(left)

        val right = Body()
        right.addFixture(Geometry.createRectangle(0.01, simHeight))
        right.translate(simWidth, simHeight / 2)
        right.setMass(MassType.INFINITE)
        world.addBody(right)

        val cl: CollisionListener<Body, BodyFixture> =
            object : CollisionListenerAdapter<Body, BodyFixture>() {
                override fun collision(collision: NarrowphaseCollisionData<Body, BodyFixture?>): Boolean {
                    val b1: Body = collision.body1
                    val b2: Body = collision.body2

                    if(b1 is Ball && b2 is Ball && b1.radius == b2.radius) {
                        val x = (b1.transform.translationX + b2.transform.translationX) / 2 * METER_TO_PIXEL
                        val y = (b1.transform.translationY + b2.transform.translationY) / 2 * METER_TO_PIXEL
                        val radius = b1.radius * 1.25
                        val ball = Ball(
                            x.coerceAtMost(getScreenWidth() - radius * METER_TO_PIXEL)
                                .coerceAtLeast(radius * METER_TO_PIXEL),
                            y.coerceAtMost(getScreenHeight() - radius * METER_TO_PIXEL)
                                .coerceAtLeast(radius * METER_TO_PIXEL),
                            radius
                        )

                        ball.setLinearVelocity(
                            (b1.linearVelocity.x + b2.linearVelocity.x) / 2,
                            (b1.linearVelocity.y + b2.linearVelocity.y) / 2
                        )

                        merges.add(MergeEvent(b1, b2, ball))
                    }

                    return super.collision(collision)
                }
            }

        world.addCollisionListener(cl)


        val timer = Timer()
        val monitor = object : TimerTask() {
            override fun run() {
                update()
            }
        }
        timer.schedule(monitor, 1000/30, 1000/30)
    }

    fun update() {
        postInvalidate()
    }

    private var lastUpdate: Long = System.nanoTime()
    override fun onDraw(canvas: Canvas) {
        val current = System.nanoTime()
        val delta = (current - lastUpdate).toDouble() / 1e9
        lastUpdate = current

        world.update(delta)

        // Handle Merges
        for (merge: MergeEvent in merges) {
            this.world.removeBody(merge.one)
            this.world.removeBody(merge.two)
            this.world.addBody(merge.new)
        }
        merges.clear()

        for (ball in world.bodyIterator) {
            if(ball !is Ball) continue
            canvas.drawCircle(
                (ball.transform.translationX.toFloat() * METER_TO_PIXEL).toFloat(),
                (ball.transform.translationY.toFloat() * METER_TO_PIXEL).toFloat(),
                (ball.radius * METER_TO_PIXEL).toFloat(), ballPaint)
        }
    }

    private var lastTouch = System.nanoTime()
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = System.nanoTime()
        val delta = (current - lastTouch).toDouble() / 1e9
        if(delta < 0.5) return true
        lastTouch = current

        if(event.action != MotionEvent.ACTION_DOWN) {
            return true
        }

        val x = event.x.toDouble()

        world.addBody(Ball(x, 50.0, 0.1))
        postInvalidate()

        return true
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }
    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }
}
