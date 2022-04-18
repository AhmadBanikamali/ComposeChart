package sys.moeinwallet.composechart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

@Composable
fun LineChart(
    dataList: List<Pair<Float, Float>>
    = listOf(
        Pair(0f, 2f),
        Pair(3.0f, 8.5f),
        Pair(5f, 5f),
        Pair(10.5f, 2f),
        Pair(13f, 10f),
        Pair(15.5f, 11.0f),
        Pair(18.5f, 15f),
        Pair(21f, 11f),
        Pair(25f, 11f),
        Pair(30f, 11f),
    ),
) {

    val arrowHead = ImageBitmap.imageResource(id = R.drawable.arrow_head)
    val arrowHeight = 30
    val arrowWidth = arrowHeight * arrowHead.width / arrowHead.height

    var horizontalDistanceBetweenBoxAndAxis by remember {
        mutableStateOf(0f)
    }

    var verticalDistanceBetweenBoxAndAxis by remember {
        mutableStateOf(0f)
    }

    Canvas(modifier = Modifier
        .height(400.dp)
        .fillMaxWidth()) {

        val graphWidth = size.width * 0.9.toFloat()
        val graphHeight = size.height
        val leftOffset = (size.width - graphWidth) / 2
        val chartTopLeft = Offset(leftOffset, 50f)
        val bottomLeftOffset = Offset(0f, graphHeight) + chartTopLeft

        val xList = mutableListOf<Float>()
        val yList = mutableListOf<Float>()


        dataList.forEach {
            xList.add(it.first)
            yList.add(it.second)
        }

        val xScaleFactor = ceil((xList.maxOrNull()!! - xList.minOrNull()!!) / dataList.size)
        val yScaleFactor = ceil((yList.maxOrNull()!! - yList.minOrNull()!!) / dataList.size)


        val axisCrossOffset = Offset(bottomLeftOffset.x + horizontalDistanceBetweenBoxAndAxis,
            bottomLeftOffset.y - verticalDistanceBetweenBoxAndAxis)
        val xAxisEndOffset =
            Offset(bottomLeftOffset.x + graphWidth - horizontalDistanceBetweenBoxAndAxis,
                bottomLeftOffset.y - verticalDistanceBetweenBoxAndAxis)
        val yAxisEndOffset = Offset(chartTopLeft.x + horizontalDistanceBetweenBoxAndAxis,
            chartTopLeft.y + verticalDistanceBetweenBoxAndAxis)

        drawRect(
            topLeft = chartTopLeft,
            size = Size(graphWidth, graphHeight),
            color = Color.Black,
            style = Stroke(5f)
        )

        drawLine(
            color = Color.Black,
            strokeWidth = 5f,
            start = axisCrossOffset,
            end = xAxisEndOffset
        )


        drawLine(
            color = Color.Black,
            strokeWidth = 5f,
            start = axisCrossOffset,
            end = yAxisEndOffset
        )

        drawImage(
            image = arrowHead,
            dstSize = IntSize(arrowWidth, arrowHeight),
            dstOffset = IntOffset(
                yAxisEndOffset.x.toInt() - arrowWidth / 2,
                yAxisEndOffset.y.toInt() - arrowHeight / 2
            )
        )

        rotate(90f, pivot = xAxisEndOffset) {
            drawImage(
                image = arrowHead,
                dstSize = IntSize(arrowWidth, arrowHeight),
                dstOffset = IntOffset(
                    xAxisEndOffset.x.toInt() - arrowWidth / 2,
                    xAxisEndOffset.y.toInt() - arrowHeight / 2
                ),
            )
        }

        val dataCount = dataList.size
        val tickLength = 20f
        val offsetFactor = 1f
        val horizontalSegmentLength =
            (xAxisEndOffset - axisCrossOffset).getDistance() * offsetFactor / dataCount.toFloat()
        val verticalSegmentLength =
            (yAxisEndOffset - axisCrossOffset).getDistance() * offsetFactor / dataCount.toFloat()
        val chartLinePoints = mutableListOf<Offset>()

        dataList.forEachIndexed { tickIndex, eachData ->

            val tickStartOffsetXAxis =
                Offset(axisCrossOffset.x + horizontalSegmentLength * tickIndex,
                    axisCrossOffset.y - tickLength / 2)
            val tickEndOffsetXAxis = Offset(axisCrossOffset.x + horizontalSegmentLength * tickIndex,
                axisCrossOffset.y + tickLength / 2)

            val tickStartOffsetYAxis = Offset(axisCrossOffset.x - tickLength / 2,
                axisCrossOffset.y - verticalSegmentLength * tickIndex)
            val tickEndOffsetYAxis = Offset(axisCrossOffset.x + tickLength / 2,
                axisCrossOffset.y - verticalSegmentLength * tickIndex)

            val pointOffset = Offset(
                (eachData.first / xScaleFactor) * horizontalSegmentLength + axisCrossOffset.x,
                (-1 * eachData.second / yScaleFactor) * verticalSegmentLength + axisCrossOffset.y,
            )


            drawLine(
                color = Color.Black,
                strokeWidth = 5f,
                start = tickStartOffsetXAxis,
                end = tickEndOffsetXAxis
            )


            drawLine(
                color = Color.Black,
                strokeWidth = 5f,
                start = tickStartOffsetYAxis,
                end = tickEndOffsetYAxis
            )

            println(pointOffset)
            chartLinePoints.add(pointOffset)
            drawCircle(center = pointOffset, radius = 10f, color = Color.Red)


            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.RED
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                val textBound = android.graphics.Rect()

                textPaint.getTextBounds("AAA",0,3,textBound)
                verticalDistanceBetweenBoxAndAxis = textBound.height().toFloat() + tickLength * 1.5f
                horizontalDistanceBetweenBoxAndAxis = textBound.width()+ tickLength*1.5f

                drawText(
                    eachData.first.toString(),
                    pointOffset.x-textBound.width()/2,
                    tickEndOffsetXAxis.y+ textBound.height(),
                    textPaint
                )

                drawText(
                    eachData.second.toString(),
                    tickStartOffsetYAxis.x - textBound.width()+tickLength,
                    pointOffset.y,textPaint
                )
            }
        }

        val line = Path().apply {
            chartLinePoints.forEachIndexed { index, it ->
                if (index == 0)
                    moveTo(it.x, it.y)
                lineTo(it.x, it.y)
            }
        }

        drawPath(line,
            color = Color.Black,
            style = Stroke(5f, pathEffect = PathEffect.cornerPathEffect(50f)))


    }
}
