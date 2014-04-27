class Kalman
	def initialize(guess)
		@gain = 1.0
		@previous_estimate = guess

	end

	def value(observation)
		estimate = @gain*observation+(1.0-@gain)*@previous_estimate
		@previous_estimate = estimate
		estimate
	end

	




end