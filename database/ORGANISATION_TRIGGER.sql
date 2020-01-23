
CREATE TRIGGER EVENT_CANCELED_TRIGGER
    ON EVENT_CANCELED
    FOR INSERT
    AS
    BEGIN
		Declare @cursorEvent cursor
		Declare @value int
		Declare @PassE int

		set @cursorEvent = cursor for 
		select PassE from inserted

		open @cursorEvent
		fetch next from @cursorEvent into @PassE

		WHILE @@FETCH_STATUS = 0 
		BEGIN
			
			SELECT @value=sum(coalesce(B.Price,T.OfficialPrice))
			FROM VALID V JOIN TICKET T ON V.PassT=T.PassT LEFT JOIN BOUGHT B ON T.PassT=B.PassT
			WHERE PassE=@PassE AND Status='P'


			update EVENT_CANCELED set Damage = @value where PassE = @PassE

			fetch next from @cursorEvent into @PassE
		END
		close @cursorEvent
		deallocate @cursorEvent
    END
go
