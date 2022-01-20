package tv.accedo.dishonstream2.domain.di

import org.koin.dsl.module
import tv.accedo.dishonstream2.domain.usecase.channel.GetChannelByChannelIdUseCase
import tv.accedo.dishonstream2.domain.usecase.channel.GetDRMTokenUseCase
import tv.accedo.dishonstream2.domain.usecase.channel.GetEPGUseCase
import tv.accedo.dishonstream2.domain.usecase.home.*
import tv.accedo.dishonstream2.domain.usecase.home.game.*
import tv.accedo.dishonstream2.domain.usecase.player.GetLicenseDetailsUseCase
import tv.accedo.dishonstream2.domain.usecase.program.GetCurrentProgramByChannelIdUseCase
import tv.accedo.dishonstream2.domain.usecase.program.GetCurrentProgramByChannelUseCase
import tv.accedo.dishonstream2.domain.usecase.program.GetProgramInfoUseCase
import tv.accedo.dishonstream2.domain.usecase.search.GetRecentSearchFieldsUseCase
import tv.accedo.dishonstream2.domain.usecase.search.GetSearchProgrameResultsUseCase
import tv.accedo.dishonstream2.domain.usecase.search.SetRecentSearchFieldsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.GetSettingsOptionsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.GetAppSettingsOptionsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.faq.GetFAQUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTemperatureFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.GetTimeFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.SetTemperatureFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.formatoptions.SetTimeFormatUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.GetVideoQualityUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.IsAutoPlayNextEpisodeEnabledUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.SetAutoPlayNextEpisodeUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.playback.SetVideoQualityUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.tvguide.GetTVGuideStyleUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.appsettings.tvguide.SetTVGuideStyleUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.legalandabout.*
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.GetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.SetPermittedClassificationsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin.GetParentalControlPinUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin.GetParentalPinValidationUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.parentalcontrols.createpin.SetParentalControlPinUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.theme.EnableLargeFontSizeUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.theme.GetThemingOptionsUseCase
import tv.accedo.dishonstream2.domain.usecase.settings.theme.IsLargeFontSizeEnabledUseCase
import tv.accedo.dishonstream2.domain.usecase.splash.InitializeAppUseCase
import tv.accedo.dishonstream2.domain.usecase.vod.*

val interactionModule = module {

    // channel
    factory { GetChannelByChannelIdUseCase(get()) }
    factory { GetEPGUseCase(get()) }
    factory { GetDRMTokenUseCase(get()) }


    // home
    factory { GetHomePageBackgroundUrlUseCase(get()) }
    factory { GetHomePageTabsUseCase(get()) }
    factory { GetHomeTemplateUseCase(get(), get()) }
    factory { GetHomeTemplateTypeUseCase(get()) }
    factory { GetPropertyLogoUrlUseCase(get()) }
    factory { GetWeatherDetailUseCase(get(), get()) }

    // game
    factory { GetCurrentGamesUseCase(get()) }
    factory { GetMLBStatsUseCase(get()) }
    factory { GetNBAStatsUseCase(get()) }
    factory { GetNFLStatsUseCase(get()) }
    factory { GetNHLStatsUseCase(get()) }


    // player
    factory { GetLicenseDetailsUseCase(get()) }


    // program
    factory { GetCurrentProgramByChannelIdUseCase(get(), get()) }
    factory { GetCurrentProgramByChannelUseCase() }
    factory { GetProgramInfoUseCase(get()) }


    //settings
    factory { GetSettingsOptionsUseCase(get()) }
    factory { GetAppSettingsOptionsUseCase(get()) }

    // faq
    factory { GetFAQUseCase(get()) }

    //temperature format
    factory { GetTemperatureFormatUseCase(get()) }
    factory { SetTemperatureFormatUseCase(get()) }

    //time format
    factory { GetTimeFormatUseCase(get()) }
    factory { SetTimeFormatUseCase(get()) }

    //playback
    factory { GetVideoQualityUseCase(get()) }
    factory { SetVideoQualityUseCase(get()) }
    factory { IsAutoPlayNextEpisodeEnabledUseCase(get()) }
    factory { SetAutoPlayNextEpisodeUseCase(get()) }

    // tv guide
    factory { GetTVGuideStyleUseCase(get(), get()) }
    factory { SetTVGuideStyleUseCase(get()) }

    // legal and about
    factory { GetAppVersionUseCase(get()) }
    factory { GetPrivacyPolicyUseCase(get()) }
    factory { GetTermsOfServiceUseCase(get()) }
    factory { GetContactUseCase(get()) }
    factory { GetDeviceInformationUseCase() }

    // theme and config
    factory { GetThemingOptionsUseCase(get()) }
    factory { IsLargeFontSizeEnabledUseCase(get()) }
    factory { EnableLargeFontSizeUseCase(get()) }

    // search
    factory { GetRecentSearchFieldsUseCase(get()) }
    factory { SetRecentSearchFieldsUseCase(get()) }
    factory { GetSearchProgrameResultsUseCase(get()) }

    // VOD
    factory { GetPopularContentUseCase(get()) }
    factory { GetTrendingContentsUseCase(get()) }
    factory { GetPopularMoviesUseCase(get()) }
    factory { GetPopularShowsUseCase(get()) }
    factory { GetMovieDetailsUseCase(get()) }
    factory { GetShowDetailsCase(get()) }
    factory { GetSeasonDetailsCase(get()) }
    factory { SearchVODContentUseCase(get()) }
    factory { GetEpisodeDetailsUseCase(get()) }

    // app init
    factory { InitializeAppUseCase(get(), get(), get()) }

    // parental controls permitted classifications
    factory { GetPermittedClassificationsUseCase(get()) }
    factory { SetPermittedClassificationsUseCase(get()) }
    factory { GetParentalControlPinUseCase(get()) }
    factory { SetParentalControlPinUseCase(get()) }
    factory { GetParentalPinValidationUseCase(get()) }
}