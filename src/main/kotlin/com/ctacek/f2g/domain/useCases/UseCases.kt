package com.ctacek.f2g.domain.useCases

import com.ctacek.f2g.domain.useCases.game.*
import com.ctacek.f2g.domain.useCases.movie.GetRandomMovies
import com.ctacek.f2g.domain.useCases.rooms.*
import com.ctacek.f2g.domain.useCases.users.DeleteUserUseCase
import com.ctacek.f2g.domain.useCases.users.GetUserDetailsUseCase
import com.ctacek.f2g.domain.useCases.users.UpdateUserUseCase
import com.ctacek.f2g.domain.useCases.users.auth.LoginViaUsernameUseCase
import com.ctacek.f2g.domain.useCases.users.auth.RefreshTokenUseCase
import com.ctacek.f2g.domain.useCases.users.auth.SignUpViaUsernameUseCase
import com.ctacek.f2g.domain.useCases.utils.GetAvatarsList

class UseCases {
    val createRoomUseCase = CreateRoomUseCase()
    val deleteRoomUseCase = DeleteRoomUseCase()
    val getRoomDetailsUseCase = GetRoomDetailsUseCase()
    val updateRoomUseCase = UpdateRoomUseCase()
    val getUserRoomsUseCase = GetUserRoomsUseCase()

    val signUpViaUsernameUseCase = SignUpViaUsernameUseCase()
    val loginViaUsernameUseCase = LoginViaUsernameUseCase()
    val refreshTokenUseCase = RefreshTokenUseCase()
    val deleteUserUseCase = DeleteUserUseCase()
    val updateUserUseCase = UpdateUserUseCase()
    val getUserDetailsUseCase = GetUserDetailsUseCase()

    val joinRoomUseCase = JoinRoomUseCase()
    val startGameUseCase = StartGameUseCase()
    val stopGameUseCase = StopGameUseCase()
    val getGameInfoUseCase = GetGameInfoUseCase()

    val getRandomMovies = GetRandomMovies()

    val getAvailableAvatarsUseCase = GetAvatarsList()
}