package com.davidrevolt.feature.home


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.davidrevolt.core.model.LocationWithRelatedData
import com.davidrevolt.feature.home.components.CustomPagerIndicator
import com.davidrevolt.feature.home.components.DynamicSkyBackground
import com.davidrevolt.feature.home.components.HomeTopAppBar
import com.davidrevolt.feature.home.components.NoSavedLocationsMessage
import com.davidrevolt.feature.home.components.TempDisplay
import com.davidrevolt.feature.home.components.cards.DailyForecastsCard
import com.davidrevolt.feature.home.components.cards.InfoCard
import com.davidrevolt.feature.home.components.cards.POICard

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(
    navigateToLocationsManagement: () -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val onPullToSync = viewModel::syncAllRelatedData

    // Animated blur effect for transition between Loading state and Data state
    val blurValue by animateFloatAsState(
        targetValue = if (uiState is HomeUiState.Loading) 50f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    DynamicSkyBackground(blurValue) {
        when (uiState) {
            is HomeUiState.Data -> {
                val data = uiState as HomeUiState.Data
                HomeScreenContent(
                    locationIdToFocusOn = data.locationIdToFocusOn,
                    locationsWithRelatedData = data.locationsWithRelatedData,
                    isSyncing = data.isSyncing,
                    onPullToSync = onPullToSync,
                    navigateToLocationsManagement = navigateToLocationsManagement
                )
            }

            is HomeUiState.Loading -> {}
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    locationIdToFocusOn: String?,
    locationsWithRelatedData: List<LocationWithRelatedData>,
    isSyncing: Boolean,
    onPullToSync: () -> Unit,
    navigateToLocationsManagement: () -> Unit,
) {
    if (locationsWithRelatedData.isEmpty())
        return NoSavedLocationsMessage(navigateToLocationsManagement)

    val pagerState = rememberPagerState(pageCount = { locationsWithRelatedData.size })
    // each page have a lazy list, this sets same scroll position for all pages
    val sharedListState = rememberLazyListState()
    val pullToSyncState = rememberPullToRefreshState()

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(locationIdToFocusOn) {
        val index =
            locationsWithRelatedData.indexOfFirst { it.location.id == locationIdToFocusOn }
        if (index != -1) {
            pagerState.animateScrollToPage(index)
        }
    }

    val locationWithRelatedData =
        // The Location with related data currently should viewed in the pager
        locationsWithRelatedData.getOrElse(pagerState.currentPage) { locationsWithRelatedData[0] }

    var tempDisplayBottomOffset by remember { mutableFloatStateOf(1f) }
    var dailyForecastsUpperOffset by remember { mutableFloatStateOf(0f) }
    val showTempDisplay by remember {
        derivedStateOf { dailyForecastsUpperOffset >= tempDisplayBottomOffset }
    }

    // space between TempDisplay to DailyForecastsCard so only 3 items of forecasts will be visible at the bottom
    val spacerHeight = 490.dp

    val cloudCoverAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cloud_cover_animation))
    val precipitationAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.precipitation_animation))
    val windSpeedAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.wind_speed_animation))
    val windDirectionAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.wind_direction_animation))

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            HomeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = locationWithRelatedData.location.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        CustomPagerIndicator(
                            pagerState = pagerState,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                },
                onAddIconClick = navigateToLocationsManagement,
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isSyncing,
            onRefresh = onPullToSync,
            state = pullToSyncState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            locationWithRelatedData.weather?.dailyForecasts?.takeIf { it.isNotEmpty() }
                ?.let { dailyForecasts ->
                    AnimatedVisibility(
                        visible = showTempDisplay,
                        modifier = Modifier.align(Alignment.TopCenter),
                        enter = fadeIn(tween(300)),
                        exit = fadeOut(tween(300))
                    ) {
                        TempDisplay(
                            modifier = Modifier
                                .fillMaxWidth()
                                // Measure Bottom y offset
                                .onGloballyPositioned { coordinates ->
                                    tempDisplayBottomOffset =
                                        coordinates.positionInParent().y + coordinates.size.height
                                },
                            dailyForecast = dailyForecasts[0]
                        )
                    }
                }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize(),
                pageSpacing = 16.dp
            ) { page ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = sharedListState,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    locationsWithRelatedData[page].weather?.dailyForecasts?.takeIf { it.isNotEmpty() }
                        ?.let { dailyForecasts ->
                            item { Spacer(modifier = Modifier.height(spacerHeight)) }
                            item { // 3-Day Forecast
                                DailyForecastsCard(
                                    modifier = Modifier
                                        // Measure Upper Y offset
                                        .onGloballyPositioned { coordinates ->
                                            dailyForecastsUpperOffset =
                                                coordinates.positionInParent().y
                                        },
                                    dailyForecasts = dailyForecasts.take(3)
                                )
                            }
                            item { // Cloud Cover & Precipitation
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    InfoCard(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                            .height(150.dp),
                                        title = "CLOUD COVER",
                                        value = "${dailyForecasts[0].cloudCoverTotal}%",
                                        visual = {
                                            LottieAnimation(
                                                composition = cloudCoverAnimation,
                                                clipSpec = LottieClipSpec.Progress(0.25f, 1f),
                                                speed = 0.3f,
                                                reverseOnRepeat = true,
                                                iterations = LottieConstants.IterateForever,
                                                contentScale = ContentScale.Fit,
                                                alignment = Alignment.BottomEnd
                                            )
                                        }
                                    )
                                    InfoCard(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                            .height(150.dp),
                                        title = "PRECIPITATION",
                                        value = "${dailyForecasts[0].precipitationTotal}mm",
                                        visual = {
                                            LottieAnimation(
                                                composition = precipitationAnimation,
                                                speed = 0.3f,
                                                iterations = LottieConstants.IterateForever,
                                                contentScale = ContentScale.Fit,
                                                alignment = Alignment.BottomEnd
                                            )
                                        }
                                    )
                                }
                            }
                            item { // Wind Speed & Wind Direction
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    InfoCard(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                            .height(150.dp),
                                        title = "WIND SPEED",
                                        value = "${dailyForecasts[0].windSpeed}m/s",
                                        visual = {
                                            LottieAnimation(
                                                composition = windSpeedAnimation,
                                                speed = 0.3f,
                                                iterations = LottieConstants.IterateForever,
                                                contentScale = ContentScale.Fit,
                                                alignment = Alignment.BottomEnd
                                            )
                                        }
                                    )
                                    InfoCard(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                            .height(150.dp),
                                        title = "WIND DIRECTION",
                                        value = dailyForecasts[0].windDirection,
                                        visual = {
                                            LottieAnimation(
                                                composition = windDirectionAnimation,
                                                speed = 0.3f,
                                                iterations = LottieConstants.IterateForever,
                                                contentScale = ContentScale.Fit,
                                                alignment = Alignment.BottomEnd
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    item { // POI
                        POICard(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            pointsOfInterest = locationWithRelatedData.pointsOfInterest
                        )
                    }
                }
            }
        }
    }
}