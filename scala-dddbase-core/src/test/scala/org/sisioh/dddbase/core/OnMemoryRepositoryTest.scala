/*
 * Copyright 2010 TRICREO, Inc. (http://tricreo.jp/)
 * Copyright 2011 Sisioh Project and others. (http://www.sisioh.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.sisioh.dddbase.core

import org.junit.runner.RunWith

import org.scalatest.{ AbstractSuite, FunSuite }
import org.scalatest.junit.{ AssertionsForJUnit, JUnitRunner }
import org.junit.Test
import java.util.UUID
import scalaz.Identity

/**
 * [[jp.tricreo.scala.ddd.base.lifecycle.OnMemoryRepository]]のためのテスト。
 *
 * @author j5ik2o
 */
class OnMemoryRepositoryTest extends AssertionsForJUnit {

  class EntityImpl(val identity: Identity[UUID]) extends Entity[UUID] with EntityCloneable[EntityImpl,UUID]

  import org.mockito.Mockito._

  val id = Identity(UUID.randomUUID())

  @Test
  def storeText {
    val e = spy(new EntityImpl(id))
    val repository = new OnMemoryRepository[EntityImpl, UUID]()
    repository.store(e)
    verify(e, atLeast(1)).identity
    assert(repository.contains(e))
  }

  @Test
  def resolveTest {
    val repository = new OnMemoryRepository[EntityImpl,UUID]()
    intercept[EntityNotFoundException] {
      repository.resolve(id)
    }
    val e = spy(new EntityImpl(id))
    repository.store(e)
    verify(e, atLeastOnce).identity

    val resolve2 = repository.resolve(id)
    assert(resolve2 != null)
    assert(resolve2.identity == id)
  }

  @Test
  def deleteTest {
    val e = spy(new EntityImpl(id))
    val repository = new OnMemoryRepository[EntityImpl,UUID]()
    intercept[EntityNotFoundException] {
      repository.delete(id)
    }

    assert(repository.contains(id) == false)
    repository.store(e)
    verify(e, atLeast(1)).identity

    repository.delete(e)
    verify(e, atLeast(1)).identity
  }

  @Test
  def cloneTest {
    val e = spy(new EntityImpl(id))
    val repository = new OnMemoryRepository[EntityImpl,UUID]()
    repository.store(e)
    val cloneRepository = repository.clone
    assert(repository == cloneRepository)
    assert(repository ne cloneRepository)
    assert(repository.entities ne cloneRepository.entities)
  }
}