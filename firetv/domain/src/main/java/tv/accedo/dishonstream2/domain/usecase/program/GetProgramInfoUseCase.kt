package tv.accedo.dishonstream2.domain.usecase.program

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.accedo.dishonstream2.domain.model.supair.ProgramInfo
import tv.accedo.dishonstream2.domain.repository.DishRepository

class GetProgramInfoUseCase(
    private val dishRepository: DishRepository
) {
    suspend operator fun invoke(echoStarId: String): ProgramInfo = withContext(Dispatchers.IO) {
        dishRepository.getProgramInfo(echoStarId)
    }
}