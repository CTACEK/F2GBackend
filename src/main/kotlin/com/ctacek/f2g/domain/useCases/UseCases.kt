package com.ctacek.f2g.domain.useCases

import com.ctacek.f2g.domain.useCases.game.*
import com.ctacek.f2g.domain.useCases.rooms.*
import com.ctacek.f2g.domain.useCases.users.DeleteUserUseCase
import com.ctacek.f2g.domain.useCases.users.GetUserDetailsUseCase
import com.ctacek.f2g.domain.useCases.users.UpdateUserUseCase
import com.ctacek.f2g.domain.useCases.users.auth.LoginViaEmailUseCase
import com.ctacek.f2g.domain.useCases.users.auth.RefreshTokenUseCase
import com.ctacek.f2g.domain.useCases.users.auth.SignUpViaEmailUseCase
import com.ctacek.f2g.domain.useCases.utils.GetAvatarsList

class UseCases {
    val createRoomUseCase = CreateRoomUseCase()
    val deleteRoomUseCase = DeleteRoomUseCase()
    val getRoomDetailsUseCase = GetRoomDetailsUseCase()
    val updateRoomUseCase = UpdateRoomUseCase()
    val getUserRoomsUseCase = GetUserRoomsUseCase()

    val signUpViaEmailUseCase = SignUpViaEmailUseCase()
    val loginViaEmailUseCase = LoginViaEmailUseCase()
    val refreshTokenUseCase = RefreshTokenUseCase()
    val deleteUserUseCase = DeleteUserUseCase()
    val updateUserUseCase = UpdateUserUseCase()
    val getUserDetailsUseCase = GetUserDetailsUseCase()

    val joinRoomUseCase = JoinRoomUseCase()
    val leaveRoomUseCase = LeaveRoomUseCase()
    val kickUserUseCase = KickUserUseCase()
    val startGameUseCase = StartGameUseCase()
    val stopGameUseCase = StopGameUseCase()
    val getGameInfoUseCase = GetGameInfoUseCase()
    val acceptUserUseCase = AcceptUserUseCase()

    val getAvailableAvatarsUseCase = GetAvatarsList()
}