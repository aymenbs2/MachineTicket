package com.aymendev.tickedmachine

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.aymendev.tickedmachine.data.CreditCard
import com.aymendev.tickedmachine.ui.theme.Gray30
import com.aymendev.tickedmachine.ui.theme.Gray40
import com.aymendev.tickedmachine.ui.theme.TickedMachineTheme
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
                val alpha = animateFloatAsState(
                    targetValue = if (isSelectedCard.value) 0f else 1f,
                    label = ""
                )
                Scaffold(topBar = {

                    Box(
                        Modifier
                            .padding(start = 20.dp, top = 30.dp, bottom = 20.dp, end = 20.dp)
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
                }) {
                    ConstraintLayout(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                    ) {
                        val (contentHeaderRf, menuTabsRf, cardsRf, cardContainerRf) = createRefs()
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
                                })

                        CardSwipeView(modifier = Modifier.constrainAs(cardsRf) {
                            bottom.linkTo(parent.bottom)

                        }, cards.value) {
                            isSelectedCard.value = it
                        }

                    }
                }

            }
        }

    }

    @Composable
    fun CardContainer(modifier: Modifier) {
        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Card(modifier=Modifier.padding(10.dp).height(320.dp), shape = RoundedCornerShape(15.dp), elevation =CardDefaults.cardElevation( 2.dp),  colors = CardDefaults.cardColors(Color.White)) {
                Box(Modifier.fillMaxSize()) {

                Row (modifier= Modifier
                    .padding(15.dp)
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.SpaceEvenly){
                    Text(text = "Enter CVV Number ")
                    Box (
                        Modifier
                            .size(162.dp, 42.dp)
                            .background(color = Gray30, shape = RoundedCornerShape(10.dp))){

                    }
                }
                }
            }
            Image(painter = painterResource(id = R.drawable.keyb), contentDescription ="" )
        }



    }


    @Composable
    fun CardSwipeView(
        modifier: Modifier,
        cards: MutableList<CreditCard>,
        onSelectItemChange: (Boolean) -> Unit
    ) {
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

        Box(
            modifier = modifier
                .fillMaxWidth()
                .rotate(-90f)
                .clickable {
                    scope.launch {
                        if (swipedCount.intValue > 0) {
                            xOffsets[xOffsets.size - swipedCount.intValue].animateTo(
                                targetValue = 0f,
                                animationSpec = tween(500)
                            )
                            swipedCount.intValue--
                        }
                    }

                },
            contentAlignment = Alignment.Center
        ) {

            cards.forEachIndexed { index, card ->
                val offset = xOffsets[index].value
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(paddings[index].value.dp)
                        .fillMaxWidth()
                        .graphicsLayer {
                            rotationZ = rotations[index].value
                        }
                        .offset(
                            x = (offset + (if (selected[index]) 0 else index * -30)).dp,
                            y = yOffsets[index].value.dp
                        )
                        .clickable {
                            selected[index] = !selected[index]
                            scope.launch {
                                rotations[index].animateTo(
                                    targetValue = if (selected[index]) 90f else 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessVeryLow
                                    )
                                )

                            }
                            scope.launch {
                                yOffsets[index].animateTo(
                                    targetValue = if (selected[index]) -370f else 0f,
                                    animationSpec = tween(400)
                                )


                            }
                            scope.launch {
                                paddings[index].animateTo(
                                    targetValue = if (selected[index]) 20f else 0f,
                                    animationSpec = tween(500)
                                )

                            }
                            for (otherIndex in xOffsets.indices) {
                                scope.launch {
                                    xOffsets[otherIndex].animateTo(
                                        targetValue = if (selected[index] && index != otherIndex) -1000f else 0f,
                                        animationSpec = tween(1000)
                                    )
                                }
                            }
                            onSelectItemChange(selected[index])
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    Log.d("TAG", "CardSwipeView: ${xOffsets[index].value}")
                                    scope.launch {
                                        if (xOffsets[index].value < -500) {
                                            xOffsets[index].animateTo(
                                                targetValue = -1000f,
                                                animationSpec = tween(500)
                                            )
                                            swipedCount.intValue++
                                        } else {
                                            Log.d("TAG", "CardSwipeView: ")
                                            xOffsets[index].animateTo(
                                                targetValue = 0f,
                                                animationSpec = tween(500)
                                            )
                                        }
                                    }
                                }
                            ) { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    if (dragAmount.x < 0)
                                        xOffsets[index].snapTo(xOffsets[index].value + dragAmount.x)
                                }
                            }
                        },
                    painter = painterResource(id = card.image),
                    contentDescription = null
                )
            }
        }
    }
}

