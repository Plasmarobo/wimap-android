Observation = Struct.new(:value, :deviation)

class Filter

	def initialize(length)
		@max_length = length

		@deviation_sum = 0.0
		@weight_sum = 0.0
		@queue = []
		length.times do |x|
			@queue.push(Observation.new(-50.0,0.0))
			@weight_sum += x.to_f
		end
	end

	def add(value)
		@queue.shift
		deviation = value.to_f - @queue.last.value.to_f
		@queue.push(Observation.new(value.to_f, deviation))
	end

	def value
		sum = 0.0
		if @queue.length == @max_length
			total_deviation = 0.0
			@queue.each do |observation|
				total_deviation += observation.deviation.abs
			end 
			
			
			@queue.each_with_index do |observation, index|
				sum += observation.value
			end
			sum /= @queue.size
			#sum *= total_deviation.abs
			puts "Filter: #{sum} from #{@queue.last.value}"
		end
		sum
	end

	def filter(value)
		self.add(value)
		self.value
	end
end