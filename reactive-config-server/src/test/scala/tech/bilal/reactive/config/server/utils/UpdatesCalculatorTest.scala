package tech.bilal.reactive.config.server.utils

import munit.FunSuite
import tech.bilal.reactive.config.server.models.{RegisteredService, Update}

class UpdatesCalculatorTest extends FunSuite {
  test("file present and service and env registered") {
    val files = Set("abc%dev.conf")
    val registeredServices = Set(RegisteredService("abc", "dev"))
    val updates = UpdatesCalculator.updates(files, registeredServices)
    assertEquals(updates, Set(Update("abc", Set("dev"))))
  }

  test("file present and service and env not registered") {
    val files = Set("abc%dev.conf")
    val registeredServices: Set[RegisteredService] = Set.empty
    val updates = UpdatesCalculator.updates(files, registeredServices)
    assertEquals(updates, Set.empty[Update])
  }

  test("file not present and service and env registered") {
    val files: Set[String] = Set.empty
    val registeredServices: Set[RegisteredService] =
      Set(RegisteredService("abc", "dev"))
    val updates = UpdatesCalculator.updates(files, registeredServices)
    assertEquals(updates, Set.empty[Update])
  }

  test("file not present and service and env not registered") {
    val files: Set[String] = Set.empty
    val registeredServices: Set[RegisteredService] = Set.empty
    val updates = UpdatesCalculator.updates(files, registeredServices)
    assertEquals(updates, Set.empty[Update])
  }

  test("file present, service registered but env not registered") {
    val files: Set[String] = Set("abc%dev.conf")
    val registeredServices: Set[RegisteredService] =
      Set(RegisteredService("abc", "qa"))
    val updates = UpdatesCalculator.updates(files, registeredServices)
    assertEquals(updates, Set.empty[Update])
  }

  test("multiple files present, services registered and envs registered") {
    val files: Set[String] = Set("abc%dev.conf", "abc%qa.conf")
    val registeredServices: Set[RegisteredService] =
      Set(RegisteredService("abc", "qa"), RegisteredService("abc", "dev"))
    val updates = UpdatesCalculator.updates(files, registeredServices)
    assertEquals(updates, Set(Update("abc", Set("dev", "qa"))))
  }

  test("service file present and services registered") {
    val files: Set[String] = Set("abc.conf")
    val registeredServices: Set[RegisteredService] =
      Set(RegisteredService("abc", "qa"), RegisteredService("abc", "dev"))
    val updates = UpdatesCalculator.updates(files, registeredServices)
    assertEquals(updates, Set(Update("abc", Set("dev", "qa"))))
  }

  test(
    "service file present with additional files and limited services & envs registered"
  ) {
    val files: Set[String] =
      Set("abc.conf", "abc%dev.conf", "pqr.conf", "abc%sandbox.conf")
    val registeredServices: Set[RegisteredService] =
      Set(RegisteredService("abc", "qa"), RegisteredService("abc", "dev"))
    val updates = UpdatesCalculator.updates(files, registeredServices)
    assertEquals(updates, Set(Update("abc", Set("dev", "qa"))))
  }

  test("service file present but no services registered") {
    val files: Set[String] = Set("abc.conf")
    val registeredServices: Set[RegisteredService] = Set.empty
    val updates = UpdatesCalculator.updates(files, registeredServices)
    assertEquals(updates, Set.empty[Update])
  }
}
