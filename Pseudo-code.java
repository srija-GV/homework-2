""Pseudo-code for Maekawa's algorithm""
""Yuanfeng Jia(ID #108807813)""
Pi:
	TSi: "Lamport timestamp"
	Quorum: "Voting set of processors for Pi"
	HaveVoted: "Set the value to True when the Pi has voted for a processor in quorum"
	NumVotesGet: "Number of votes get from quorum"
	Candidate: "Processor for which Pi has voted for"
	HaveInquired: "Set the value to True when Pi has tried to recall a vote"
	InCS: "Set the value to true if Pi is in CS"
	RequestQueue: "Queue of processors in quorum waiting for Pi's vote"

Request:
	NumVotesGet = 0
	RequestTS = TSi
	send(REQUEST, RequestTS, Pi) to all other processors in Quorum
	while(NumVotesGet < sizeof(Quorum)):
		on APPROVE(Pj):
			NumVotesGet += 1
		on INQUIRE(Pj, TSj):
			if RequestTS = TSj
			send(RELINQUISH, Pj)
			NumVotesGet -= 1
	InCS = True

CS:

Release:
	send(RELEASE, Pj) to all other processors in Quorum
	InCS = False
	HaveVoted = False

OnRequest(REQUEST, TSj, Pj):
	if HaveVoted = False:
		send(APPROVE, Pj)
		VoteTS = TSj
		Candidate = Pj
		HaveVoted = True
	else:
		RequestQueue.add(Pj, TSj)
		if (Pj < VoteTS) and (HaveInquired = False):
			send(INQUIRE, Candidate, TSj)
			HaveInquired = True

OnRelinquish(RELINQUISH, Pj):
	(Pk, TS) = RequestQueue.pop()
	send(APPROVE, Pk)
	Candidate = Pk


OnRelease(RELEASE, Pj):
	if RequestQueue is not empty:
		(Pk, TS) = RequestQueue.pop()
		send(APPROVE, Pk)
		Candidate = Pk
	else:
		HaveVoted = False
		HaveInquired = False

	

