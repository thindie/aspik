package com.thindie.aspik

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.thindie.aspik.feature.home.HomeFlow
import com.thindie.engine.core.Deeplink
import com.thindie.engine.core.Log
import com.thindie.engine.core.Route
import com.thindie.engine.core.Router
import com.thindie.engine.core.Section
import com.thindie.engine.uikit.AppTheme
import com.thindie.engine.uikit.LocalThemeSwitcher
import com.thindie.engine.uikit.ThemeSwitcher
import com.thindie.engine.uikit.VSpacer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private lateinit var app: Application
  private lateinit var router: Router

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    app = application as Application
    router = app.requireRouter()
    awaitFinish()
    setContent {
      LaunchedEffect(Unit) {
        val deeplink = parseIntent()
      }
      val themeSwitcher =
        remember {
          val switcher = ThemeSwitcher()
          switcher
        }
      CompositionLocalProvider(
        LocalThemeSwitcher provides themeSwitcher,
      ) {
        val themeColors = LocalThemeSwitcher.current.themeFlow.collectAsState(null)
        val isDark =
          when (themeColors.value) {
            null -> isSystemInDarkTheme()
            ThemeSwitcher.Choice.Dark -> true
            ThemeSwitcher.Choice.Light -> false
            ThemeSwitcher.Choice.Auto -> isSystemInDarkTheme()
          }
        val view = LocalView.current
        if (!view.isInEditMode) {
          SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
          }
        }
        AppTheme(isDark) {
          // Back handler disabled intentionally — native back button
          // triggers router.pop(), and when stack is empty, onPopLast()
          // emits finishCommand which closes the activity.
          BackHandler { }
          val routes by router.route.collectAsState(null)
          var prev by remember { mutableStateOf<Pair<Route, Route?>?>(null) }
          val isPop = routes != null && prev != null && routes!!.first == prev!!.second
          LaunchedEffect(routes) { prev = routes }
          if (routes != null) {
            val tween = tween<IntOffset>(durationMillis = 280)
            AnimatedContent(
              modifier = Modifier.background(AppTheme.colors.backgroundPrimary),
              targetState = routes!!.first,
              transitionSpec = {
                if (routes?.first?.section is HomeSection && routes?.second == null && !isPop) {
                  ContentTransform(
                    targetContentEnter = EnterTransition.None,
                    initialContentExit = ExitTransition.None,
                    targetContentZIndex = 0f,
                    sizeTransform = null,
                  )
                } else if (isPop) {
                  slideInHorizontally(tween) { -it } + fadeIn(tween()) togetherWith
                    slideOutHorizontally(tween) { it } + fadeOut(tween())
                } else {
                  slideInHorizontally(tween) { it } + fadeIn(tween()) togetherWith
                    slideOutHorizontally(tween) { -it } + fadeOut(tween())
                }
              },
              label = "route",
            ) { route ->
              when (route.section) {
                Section.Leaf -> route.content.invoke()
                is HomeSection -> {
                  when (route.section as HomeSection) {
                    is HomeSection.Main -> {
                      Box(modifier = Modifier.systemBarsPadding()) {
                        MainPlaceholder()
                        BottomNavigationBar(
                          modifier = Modifier.align(Alignment.BottomCenter),
                          onMainClick = { switchToMain() },
                          onSettingsClick = { switchToSettings() },
                          onSpiksClick = { switchToSpiks() },
                          selected = route.section,
                        )
                      }
                    }
                    is HomeSection.Settings -> {
                      Box(modifier = Modifier.systemBarsPadding()) {
                        SettingsPlaceholder()
                        BottomNavigationBar(
                          modifier = Modifier.align(Alignment.BottomCenter),
                          onMainClick = { switchToMain() },
                          onSettingsClick = {},
                          onSpiksClick = { switchToSpiks() },
                          selected = route.section,
                        )
                      }
                    }
                    is HomeSection.Spiks -> {
                      Box(modifier = Modifier.systemBarsPadding()) {
                        SpiksPlaceholder()
                        BottomNavigationBar(
                          modifier = Modifier.align(Alignment.BottomCenter),
                          onMainClick = { switchToMain() },
                          onSettingsClick = { switchToSettings() },
                          onSpiksClick = {},
                          selected = route.section,
                        )
                      }
                    }
                  }
                }

                else -> error("Unexpected section")
              }
            }
          }
        }
      }
    }
  }

  private suspend fun parseIntent(): Deeplink {
    val extra = intent.getStringExtra(Application.DEEPLINK)
    Log.d({ "parsing intent extra: $extra" })
    return Deeplink.NotSpecified
  }

  private fun awaitFinish() {
    lifecycleScope.launch {
      (application as Application).finishCommand.first()
      finish()
    }
  }

  private fun switchToMain() {
    val homeFlow = HomeFlow(router)
    homeFlow.onFinishBuilder { result ->
      when (result) {
        HomeFlow.Result.Spiks -> {
          switchToSpiks()
        }
        HomeFlow.Result.Success -> {
        }
        HomeFlow.Result.Settings -> {
          switchToSettings()
        }
      }
    }

    homeFlow
      .switchFlow()
  }

  private fun switchToSettings() {
  }

  private fun switchToSpiks() {
  }
}

@Composable
fun BottomNavigationBar(
  modifier: Modifier = Modifier,
  onMainClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onSpiksClick: () -> Unit,
  selected: Section,
) {
  val sections =
    remember {
      listOf(
        HomeSection.Main,
        HomeSection.Settings,
        HomeSection.Spiks,
      )
    }

  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .background(AppTheme.colors.backgroundPrimary),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    sections.forEach {
      val iconResId: Int =
        when (it) {
          HomeSection.Main -> R.drawable.ic_main
          HomeSection.Settings -> R.drawable.ic_settings
          HomeSection.Spiks -> R.drawable.ic_spiks
        }
      val titleResId: Int =
        when (it) {
          HomeSection.Main -> R.string.main_title
          HomeSection.Settings -> R.string.settings_title
          HomeSection.Spiks -> R.string.spiks_title
        }

      Section(
        title = stringResource(titleResId),
        icon = painterResource(iconResId),
        onClick = {
          when (it) {
            HomeSection.Main -> onMainClick.invoke()
            HomeSection.Settings -> onSettingsClick.invoke()
            HomeSection.Spiks -> onSpiksClick.invoke()
          }
        },
        isSelected = selected == it,
      )
    }
  }
}

@Composable
fun Section(
  modifier: Modifier = Modifier,
  title: String,
  icon: Painter,
  onClick: () -> Unit,
  isSelected: Boolean,
) {
  val color by animateColorAsState(
    targetValue =
      when {
        isSelected -> {
          AppTheme.colors.accentPrimary
        }

        else -> {
          AppTheme.colors.contentSecondary
        }
      },
    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
  )
  Column(
    modifier =
      modifier
        .clickable(
          onClick = onClick,
          indication = null,
          interactionSource = null,
        ),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    VSpacer(16.dp)
    Icon(
      painter = icon,
      contentDescription = null,
      tint = color,
      modifier = Modifier.size(40.dp),
    )
    Text(
      text = title,
      style = AppTheme.typography.labelMedium,
      color = color,
      modifier = Modifier.padding(top = 4.dp),
      maxLines = 1,
    )
    VSpacer(8.dp)
  }
}

@Composable
fun MainPlaceholder() {
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("Main", style = AppTheme.typography.headlineMedium, color = AppTheme.colors.contentPrimary)
  }
}

@Composable
fun SettingsPlaceholder() {
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("Settings", style = AppTheme.typography.headlineMedium, color = AppTheme.colors.contentPrimary)
  }
}

@Composable
fun SpiksPlaceholder() {
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("Spiks", style = AppTheme.typography.headlineMedium, color = AppTheme.colors.contentPrimary)
  }
}

@Immutable
sealed interface HomeSection : Section {
  @Immutable
  data object Main : HomeSection

  @Immutable
  data object Settings : HomeSection

  @Immutable
  data object Spiks : HomeSection
}
