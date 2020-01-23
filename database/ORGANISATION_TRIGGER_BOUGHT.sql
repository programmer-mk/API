ALTER TRIGGER COMPUTE_BOUGHT
ON BOUGHT
FOR INSERT
AS
BEGIN
	DECLARE @CursorBought CURSOR
	DECLARE @BoughtTickets INT
	DECLARE @PassB int
	DECLARE @PassT int
	DECLARE @TicketsNumCurr int
	DECLARE @OfficialPrice int
	DECLARE @DiscountVal decimal(10,2)

	SELECT @PassB = PassB, @PassT = PassT
	FROM inserted

	SELECT @BoughtTickets = COUNT(*)
	FROM BOUGHT
	WHERE PassB = @PassB

	SET @CursorBought = CURSOR FOR
	SELECT TicketsNumber FROM DISCOUNT

	SET @DiscountVal = 0.00

	OPEN @CursorBought
	FETCH NEXT FROM @CursorBought INTO @TicketsNumCurr

	WHILE @@FETCH_STATUS = 0
	BEGIN
		IF @TicketsNumCurr <= @BoughtTickets
		BEGIN
			SELECT @DiscountVal = Discount
			FROM DISCOUNT
			WHERE TicketsNumber = @TicketsNumCurr
			print(111)
		END

		FETCH NEXT FROM @CursorBought INTO @TicketsNumCurr
	END
	CLOSE @CursorBought
	DEALLOCATE @CursorBought

	SELECT @OfficialPrice = OfficialPrice
	FROM TICKET
	WHERE PassT = @PassT

	UPDATE BOUGHT SET Price = @OfficialPrice - @OfficialPrice*@DiscountVal/100
	WHERE PassT = @PassT
END
				 
