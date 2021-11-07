package id.nns.nichat.utils.converters

import id.nns.nichat.data.entity.ChannelEntity
import id.nns.nichat.data.entity.ChatEntity
import id.nns.nichat.data.entity.MessageEntity
import id.nns.nichat.data.entity.UserEntity
import id.nns.nichat.data.response.MessageResponse
import id.nns.nichat.data.response.UserResponse
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.domain.model.Chat
import id.nns.nichat.domain.model.Message
import id.nns.nichat.domain.model.User

object DataMapper {

    fun mapChannelEntityToDomain(input: List<ChannelEntity>) : List<Channel> =
        input.map {
            it.toDomain()
        }

    fun mapUserResponseToDomain(input: List<UserResponse>) : List<User> =
        input.map {
            it.toDomain()
        }

    // Entity to Domain
    private fun UserEntity.toDomain() : User =
        User(
            dob = this.dob,
            email = this.email,
            imgUrl = this.imgUrl,
            name = this.name,
            status = this.status,
            uid = this.uid
        )

    private fun MessageEntity.toDomain() : Message =
        Message(
            fromId = this.fromId,
            messageId = this.messageId,
            text = this.text,
            timeStamp = this.timeStamp,
            toId = this.toId
        )

    fun ChannelEntity.toDomain() : Channel =
        Channel(
            partnerId = this.partnerId,
            partnerUser = this.partnerUser?.toDomain(),
            latestMessage = this.latestMessage?.toDomain()
        )

    // Domain to Entity
    private fun User.toEntity() : UserEntity =
        UserEntity(
            dob = this.dob,
            email = this.email,
            imgUrl = this.imgUrl,
            name = this.name,
            status = this.status,
            uid = this.uid
        )

    private fun Message.toEntity() : MessageEntity =
        MessageEntity(
            fromId = this.fromId,
            messageId = this.messageId,
            text = this.text,
            timeStamp = this.timeStamp,
            toId = this.toId
        )

    fun Channel.toEntity() : ChannelEntity =
        ChannelEntity(
            partnerId = this.partnerId,
            partnerUser = this.partnerUser?.toEntity(),
            latestMessage = this.latestMessage?.toEntity()
        )

    fun Chat.toEntity() : ChatEntity =
        ChatEntity(
            partnerId = this.partnerId,
            messages = this.messages
        )

    // Response to Domain
    fun UserResponse.toDomain() : User =
        User(
            dob = this.dob,
            email = this.email,
            imgUrl = this.imgUrl,
            name = this.name,
            status = this.status,
            uid = this.uid
        )

    fun MessageResponse.toDomain() : Message =
        Message(
            fromId = this.fromId,
            messageId = this.messageId,
            text = this.text,
            timeStamp = this.timeStamp,
            toId = this.toId
        )

}
