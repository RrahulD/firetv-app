package tv.accedo.dishonstream2.di

import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tv.accedo.dishonstream2.ui.main.MainViewModel
import tv.accedo.dishonstream2.ui.main.home.HomeViewModel
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.CarouselWidgetViewHolderFactory
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.LargeWidgetViewHolderFactory
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.StandardWidgetViewHolderFactory
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.factory.WidgetViewHolderFactory
import tv.accedo.dishonstream2.ui.main.home.component.widget.base.util.WidgetUIHelper
import tv.accedo.dishonstream2.ui.main.home.component.widget.game.GameWidgetViewModel
import tv.accedo.dishonstream2.ui.main.home.component.widget.livechannel.LiveChannelWidgetViewModel
import tv.accedo.dishonstream2.ui.main.home.component.widget.moreinfo.MoreInfoWidgetViewModel
import tv.accedo.dishonstream2.ui.main.home.component.widget.weather.WeatherWidgetViewModel
import tv.accedo.dishonstream2.ui.main.home.dialog.game.GameStatsViewModel
import tv.accedo.dishonstream2.ui.main.home.dialog.programinfo.ProgramInfoDialogViewModel
import tv.accedo.dishonstream2.ui.main.home.sports.SportsViewModel
import tv.accedo.dishonstream2.ui.main.ondemand.OnDemandDetailsViewModel
import tv.accedo.dishonstream2.ui.main.ondemand.OnDemandViewModel
import tv.accedo.dishonstream2.ui.main.ondemand.seasondetails.SeasonDetailsViewModel
import tv.accedo.dishonstream2.ui.main.recordings.RecordingsViewModel
import tv.accedo.dishonstream2.ui.main.search.SearchViewModel
import tv.accedo.dishonstream2.ui.main.search.SeeAllViewModel
import tv.accedo.dishonstream2.ui.main.settings.SettingsViewModel
import tv.accedo.dishonstream2.ui.main.settings.appsettings.AppSettingsViewModel
import tv.accedo.dishonstream2.ui.main.settings.appsettings.temperatureformat.TemperatureFormatViewModel
import tv.accedo.dishonstream2.ui.main.settings.appsettings.tvguideformat.TVGuideFormatViewModel
import tv.accedo.dishonstream2.ui.main.settings.appsettings.videoquality.VideoQualityViewModel
import tv.accedo.dishonstream2.ui.main.settings.faqs.FaqsViewModel
import tv.accedo.dishonstream2.ui.main.settings.legalandabout.LegalAndAboutViewModel
import tv.accedo.dishonstream2.ui.main.settings.legalandabout.privacypolicy.PrivacyPolicyViewModel
import tv.accedo.dishonstream2.ui.main.settings.legalandabout.termsofservice.TermsOfServiceViewModel
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlCreatePinViewModel
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalControlsViewModel
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.ParentalPinValidationViewModel
import tv.accedo.dishonstream2.ui.main.settings.parentalcontrols.permittedclassifications.PermittedClassificationsViewModel
import tv.accedo.dishonstream2.ui.main.shared.SharedAppViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.TVGuideViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.player.PlayerViewModel
import tv.accedo.dishonstream2.ui.main.tvguide.player.view.miniepg.program.MiniEpgProgramViewHolderViewModel
import tv.accedo.dishonstream2.ui.splash.SplashViewModel
import tv.accedo.dishonstream2.ui.theme.ThemeManager


val presentationModule = module {

    // view models
    viewModel { SplashViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { PlayerViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { TVGuideViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { OnDemandViewModel(get(), get(), get()) }
    viewModel { OnDemandDetailsViewModel(get(), get(), get()) }
    viewModel { RecordingsViewModel() }
    viewModel { SearchViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SeeAllViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { TVGuideFormatViewModel(get(), get()) }
    viewModel { TemperatureFormatViewModel(get(), get()) }
    viewModel { AppSettingsViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { VideoQualityViewModel(get(), get()) }
    viewModel { FaqsViewModel(get()) }
    viewModel { LegalAndAboutViewModel(get(), get(), get()) }
    viewModel { TermsOfServiceViewModel(get()) }
    viewModel { PrivacyPolicyViewModel(get()) }
    viewModel { SharedAppViewModel(get(), get(), get(), get()) }
    viewModel { ProgramInfoDialogViewModel(get()) }
    viewModel { SportsViewModel(get()) }
    viewModel { GameStatsViewModel(get(), get(), get(), get()) }
    viewModel { PermittedClassificationsViewModel(get(), get()) }
    viewModel { ParentalControlCreatePinViewModel(get(), get()) }
    viewModel { ParentalPinValidationViewModel(get()) }
    viewModel { ParentalControlsViewModel(get(),get(), get()) }
    viewModel { SeasonDetailsViewModel(get()) }

    // viewholder viewmodel
    factory { (scope: CoroutineScope) -> LiveChannelWidgetViewModel(get(), get(), get(), scope) }
    factory { (scope: CoroutineScope) -> MoreInfoWidgetViewModel(get(), scope) }
    factory { (scope: CoroutineScope) -> WeatherWidgetViewModel(get(), get(), scope) }
    factory { (scope: CoroutineScope) -> MiniEpgProgramViewHolderViewModel(get(), get(), scope) }
    factory { (scope: CoroutineScope) -> GameWidgetViewModel(get(), scope) }

    // widget factory and helpers
    factory { StandardWidgetViewHolderFactory() }
    factory { LargeWidgetViewHolderFactory() }
    factory { CarouselWidgetViewHolderFactory() }
    factory { WidgetViewHolderFactory(get(), get(), get()) }
    factory { WidgetUIHelper() }

    // theme
    single<ThemeManager> { ThemeManager(get()) }
}