package com.aymendev.tickedmachine

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.aymendev.tickedmachine.data.CreditCard
import com.aymendev.tickedmachine.ui.compents.HorizontalClipShape
import com.aymendev.tickedmachine.ui.compents.HorizontalClipShapeLeftToRight
import com.aymendev.tickedmachine.ui.compents.HorizontalClipShapeRightToLeft
import com.aymendev.tickedmachine.ui.compents.VerticalClipShape
import com.aymendev.tickedmachine.ui.theme.Gray30
import com.aymendev.tickedmachine.ui.theme.Gray60
import com.aymendev.tickedmachine.ui.theme.Purple50
import com.aymendev.tickedmachine.ui.theme.TickedMachineTheme
import com.aymendev.tickedmachine.ui.utils.dpToPixel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TickedMachineTheme {
                val cards = remember {
                    mutableStateOf(
                        mutableListOf(
                            CreditCard(
                                id = 0,
                                image = R.drawable.blue_card,
                                isDragged = false,
                                isSelected = false
                            ),
                            CreditCard(
                                id = 1,
                                image = R.drawable.supreme_card,
                                isDragged = false,
                                isSelected = false
                            ),
                            CreditCard(
                                id = 2,
                                image = R.drawable.titan_card,
                                isDragged = false,
                                isSelected = false
                            ),
                        )
                    )
                }
                val isSelectedCard = remember {
                    mutableStateOf(false)
                }
                val isCompletedPayement = remember {
                    mutableStateOf(false)
                }

                val isPrinting = remember {
                    mutableStateOf(false)
                }
                val isInserted = remember {
                    mutableStateOf(false)
                }
                val alpha = animateFloatAsState(
                    targetValue = if (isSelectedCard.value) 0f else 1f,
                    label = ""
                )
                Scaffold(topBar = {

                    Box(
                        Modifier
                            .padding(start = 20.dp, top = 30.dp, bottom = 5.dp, end = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Image(
                            modifier = Modifier.align(Alignment.CenterStart),
                            painter = painterResource(id = R.drawable.ic_bacck),
                            contentDescription = ""
                        )
                        Image(
                            modifier = Modifier.align(Alignment.Center),
                            painter = painterResource(id = R.drawable.payments_text),
                            contentDescription = ""
                        )
                    }
                }) { padding ->
                    ConstraintLayout(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                    ) {
                        val (contentHeaderRf, menuTabsRf, cardsRf, machine, stateText, blur, smallTicket, cardContainerRf) = createRefs()
                        Image(
                            modifier = Modifier
                                .alpha(alpha.value)
                                .constrainAs(contentHeaderRf) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                },
                            contentScale = ContentScale.Crop,
                            painter = painterResource(id = R.drawable.details),
                            contentDescription = ""
                        )

                        Image(
                            modifier =
                            Modifier
                                .alpha(alpha.value)
                                .padding(top = 20.dp)
                                .constrainAs(menuTabsRf) {
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    top.linkTo(contentHeaderRf.bottom)
                                },
                            painter = painterResource(id = R.drawable.payments_methods),
                            contentDescription = ""
                        )


                        CardContainer(
                            Modifier
                                .alpha(1 - alpha.value)

                                .constrainAs(cardContainerRf) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }) {
                            isCompletedPayement.value = true
                        }
                        if (isCompletedPayement.value) {

                            Image(
                                modifier =
                                Modifier
                                    .constrainAs(machine) {
                                        start.linkTo(parent.start)
                                        top.linkTo(parent.top)
                                        end.linkTo(parent.end)
                                        bottom.linkTo(parent.bottom)
                                    },
                                contentScale = ContentScale.Crop,
                                painter = painterResource(id = R.drawable.machine),
                                contentDescription = ""
                            )
                            Text(
                                modifier = Modifier.constrainAs(stateText) {
                                    start.linkTo(machine.start)
                                    end.linkTo(machine.end)
                                    top.linkTo(machine.top, 150.dp)
                                }, text = when {
                                    (isInserted.value && !isPrinting.value) -> "Check Card"
                                    (isInserted.value && isPrinting.value) -> "Printing"
                                    else -> "Insert Your card"
                                }
                            )
                            Image(
                                modifier =
                                Modifier
                                    .padding(top = 30.dp)
                                    .fillMaxWidth()
                                    .constrainAs(blur) {
                                        bottom.linkTo(cardsRf.bottom)
                                    },
                                contentScale = ContentScale.Crop,
                                painter = painterResource(id = R.drawable.blur),
                                contentDescription = ""
                            )
                            SmallTicket(
                                Modifier
                                    .padding(top = 205.dp, end = 40.dp)
                                    .width(70.dp)
                                    .constrainAs(smallTicket) {
                                        top.linkTo(machine.top)
                                        end.linkTo(machine.end)
                                        bottom.linkTo(machine.bottom)
                                    }, isPrinting = isPrinting
                            )
                        }


                        CardSwipeView(
                            modifier = Modifier.constrainAs(cardsRf) {
                                bottom.linkTo(parent.bottom)

                            },
                            isCompletedPayement = isCompletedPayement,
                            isPrinting = isPrinting,
                            isInserted = isInserted,
                            cards = cards.value
                        ) {
                            isSelectedCard.value = it
                        }

                    }
                }

            }
        }

    }

    @Composable
    fun SmallTicket(modifier: Modifier, isPrinting: MutableState<Boolean>) {
        AnimatedVisibility(
            modifier = modifier,
            visible = isPrinting.value,
            enter = slideInVertically(tween(2000)) { -100 }) {
            Image(
                modifier = Modifier.size(60.dp, 80.dp),
                contentScale = ContentScale.Inside,
                painter = painterResource(id = R.drawable.small_ticket),
                contentDescription = ""
            )
        }
    }

    @Composable
    fun CardContainer(modifier: Modifier, onCompleteClicked: (String) -> Unit) {

        val cvs = remember {
            mutableStateOf("")
        }
        val isCompleted = remember {

            mutableStateOf(false)
        }
        AnimatedVisibility(
            visible = !isCompleted.value, exit = slideOutVertically(
                targetOffsetY = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300)
            )
        ) {

            Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    modifier = Modifier
                        .padding(10.dp)
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Column(
                        Modifier
                            .padding(vertical = 0.dp)
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            Modifier
                                .padding(15.dp)
                                .height(180.dp)
                        ) {

                        }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                                .height(100.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Enter CVV Number ")

                            Box(
                                Modifier
                                    .height(42.dp)
                                    .width(162.dp)
                                    .background(
                                        color = Gray30,
                                        shape = RoundedCornerShape(10.dp)
                                    ), contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    text = cvs.value
                                )
                            }


                        }
                        if (cvs.value.length >= 3) {
                            Button(modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .height(44.dp)
                                .fillMaxWidth(),
                                shape = RoundedCornerShape(15.dp),
                                colors = ButtonDefaults.buttonColors(Purple50),
                                onClick = {
                                    onCompleteClicked(cvs.value)
                                    isCompleted.value = true
                                }) {
                                Text(text = "Complete Payment")
                            }
                        }
                    }

                }

                CustomKeyboard(onTapKey = {
                    if (cvs.value.length < 3)
                        cvs.value += it
                })
            }
        }


    }


    @Composable
    fun CustomKeyboard(onTapKey: (String) -> Unit) {
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "")
        )

        Column(
            modifier = Modifier
                .wrapContentSize()
                .zIndex(10000f)
            // Padding to match iPhone keyboard layout
        ) {
            keys.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { key ->
                        if (key.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                            )
                        } else {
                            Key(
                                displayText = key,
                                onTapKey = onTapKey
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Key(displayText: String, onTapKey: (String) -> Unit) {

        Button(modifier = Modifier
            .padding(4.dp)
            .size(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(Gray60),
            onClick = {
                onTapKey(displayText)
            }) {
            Text(
                textAlign = TextAlign.Center,
                text = displayText,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
fun CardSwipeView(
    modifier: Modifier,
    cards: MutableList<CreditCard>,
    isCompletedPayement: MutableState<Boolean>,
    isPrinting: MutableState<Boolean>,
    isInserted: MutableState<Boolean>,
    onSelectItemChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val xOffsets =
        remember { mutableStateListOf(Animatable(0f), Animatable(0f), Animatable(0f)) }
    val yOffsets =
        remember { mutableStateListOf(Animatable(0f), Animatable(0f), Animatable(0f)) }
    val rotations =
        remember { mutableStateListOf(Animatable(0f), Animatable(0f), Animatable(0f)) }
    val paddings =
        remember { mutableStateListOf(Animatable(0f), Animatable(0f), Animatable(0f)) }
    val swipedCount = remember {
        mutableIntStateOf(0)
    }
    val selected = remember {
        mutableStateListOf(false, false, false)
    }

    if (!selected.any { it }) {
        modifier.pointerInput(Unit) {
            if (!selected.any { it })
                detectDragGestures { change, dragAmount ->
                    if (dragAmount.x >= 0)
                        scope.launch {
                            if (swipedCount.intValue > 0) {
                                xOffsets[xOffsets.size - swipedCount.intValue].animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(500)
                                )
                                swipedCount.intValue--
                            }
                        }
                }
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .rotate(-90f),
        contentAlignment = Alignment.Center
    ) {


        cards.forEachIndexed { index, card ->
            val offset = xOffsets[index].value
            val scale = animateFloatAsState(
                targetValue =
                when {
                    isCompletedPayement.value && !isInserted.value -> 0.5f
                    isInserted.value -> 0.25f
                    else -> 1F
                },
                label = ""
            )
            LaunchedEffect(
                selected[index],
                isCompletedPayement.value,
                isInserted.value,
                isPrinting.value
            ) {

                scope.launch {
                    yOffsets[index].animateTo(
                        targetValue = when {
                            selected[index]
                                    && !isCompletedPayement.value
                            ->
                                (dpToPixel(
                                    -150f,
                                    context
                                ))

                            isCompletedPayement.value && isInserted.value ->
                                (dpToPixel(
                                    45f,
                                    context
                                ))

                            else -> 0f

                        },
                        animationSpec = tween(400)
                    )

                }

                scope.launch {
                    rotations[index].animateTo(
                        targetValue = if (selected[index]
                            && !isCompletedPayement.value
                        )
                            90f
                        else if (selected[index] && isCompletedPayement.value)
                            180f
                        else
                            0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessVeryLow
                        ),
                    )


                    if (selected[index] && isCompletedPayement.value) {
                        delay(1200)
                        isInserted.value = true
                    }

                    if (isInserted.value) {
                        delay(1400)
                        isPrinting.value = true
                    }

                }
                scope.launch {
                    paddings[index].animateTo(
                        targetValue = if (selected[index]) 20f else 0f,
                        animationSpec = tween(500)
                    )

                }
                val isSelected = selected.any { it }
                for (otherIndex in xOffsets.indices) {
                    scope.launch {
                        xOffsets[otherIndex].animateTo(
                            targetValue = when {
                                (isSelected && !selected[otherIndex]) -> dpToPixel(-1000f, context)
                                (isSelected && selected[otherIndex] && isInserted.value) -> dpToPixel(
                                    -260f,
                                    context
                                )

                                else -> 0f
                            },
                            animationSpec = tween(1000)
                        )
                    }
                }
            }
            val clipPercentage = animateFloatAsState(targetValue = if (isPrinting.value) 1f else 0f,
                tween(1000)
            )
            var modifier =
                Modifier
                    .padding(paddings[index].value.dp)
                    .fillMaxWidth()
                    .scale(scale.value)
                    .graphicsLayer {
                        rotationZ = rotations[index].value
                    }
                    .offset(
                        x = (offset + (if (selected[index] || isPrinting.value) 0 else index * -30)).dp,
                        y = yOffsets[index].value.dp
                    )
                    .clickable {
                        selected[index] = !selected[index]
                        onSelectItemChange(selected[index])
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    if (xOffsets[index].value < -200) {
                                        xOffsets[index].animateTo(
                                            targetValue = -1000f,
                                            animationSpec = tween(1000)
                                        )
                                        swipedCount.intValue++
                                    } else {
                                        xOffsets[index].animateTo(
                                            targetValue = 0f,
                                            animationSpec = tween(1000)
                                        )
                                    }
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                if (dragAmount.x < 0)
                                    xOffsets[index].snapTo(xOffsets[index].value + dragAmount.x)
                                else {
                                    scope.launch {
                                        if (swipedCount.intValue > 0) {
                                            xOffsets[xOffsets.size - swipedCount.intValue].animateTo(
                                                targetValue = 0f,
                                                animationSpec = tween(500)
                                            )
                                            swipedCount.intValue--
                                        }
                                    }
                                }
                            }
                        }
                    }
            if (isPrinting.value) {
                scope.launch {
                    xOffsets[index].animateTo(  xOffsets[index].value-30, animationSpec = tween(90))
                }
                modifier = modifier.clip(HorizontalClipShapeRightToLeft(clipPercentage.value))

            }
            Image(
                contentScale = ContentScale.Crop,
                modifier = modifier,
                painter = painterResource(id = card.image),
                contentDescription = null
            )
        }
    }


}

