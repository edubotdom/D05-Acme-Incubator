/*
 * ConsumerAcmeRequestRepository.java
 *
 * Copyright (c) 2019 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.authenticated.forum;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.entities.customization.Customization;
import acme.entities.forums.Forum;
import acme.entities.participants.Participant;
import acme.entities.rounds.Round;
import acme.framework.entities.Authenticated;
import acme.framework.repositories.AbstractRepository;

@Repository
public interface AuthenticatedForumRepository extends AbstractRepository {

	@Query("select t from Forum t where t.id = ?1")
	Forum findOneForumById(int id);

	@Query("select t from Round t where t.id = ?1")
	Round findOneRoundById(int id);

	@Query("select p.user from Participant p where p.forum.id = ?1")
	List<Authenticated> findManyAuthenticatedByForumId(int id);

	@Query("select p from Participant p where p.user.id = ?1")
	Collection<Participant> findManyByUserId(int id);

	@Query("select t from Forum t where exists (select p from Participant p where p.user.id = ?1 and p.forum.id=t.id)")
	Collection<Forum> findManyForumsInParticipantByUserId(int id);

	@Query("select count(p) from Participant p where p.user.userAccount.id = ?1 and p.forum.id = ?2")
	int countAuthenticatedByForumId(int idUser, int idForum);

	@Query("select a from Authenticated a where a.userAccount.id = ?1")
	Authenticated findOneAuthenticatedByUserAccountId(int id);

	@Query("select c from Customization c")
	Customization findCustomization();
}
