package tech.bilal.reactive.config.server.webhook.models

import io.bullet.borer.Decoder
import io.bullet.borer.derivation.MapBasedCodecs

case class Commit(
    added: List[String],
    modified: List[String],
    removed: List[String]
)
case class PushHook(commits: List[Commit])

object PushHook {
  private implicit val commitDec: Decoder[Commit] = MapBasedCodecs.deriveDecoder
  implicit val dec: Decoder[PushHook] = MapBasedCodecs.deriveDecoder
}
