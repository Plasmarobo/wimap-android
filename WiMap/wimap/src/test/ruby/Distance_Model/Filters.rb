module Filters

	def moving_average(array,width)
		result = []
		queue = []
		while queue.size < width
			queue.push(array.shift)
		end
		while queue.size == width
			result << Filters::average(queue)
			queue.shift
			if !array.empty?
				queue.push(array.shift)
			end
		end
		result
	end

	def average(array)
		sum = 0.0
		array.each do |item|
			sum += item
		end
		sum /= array.size
	end
end