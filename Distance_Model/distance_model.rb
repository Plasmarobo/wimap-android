require 'csv'

class DistanceModel
	PI = 3.14
	

	def distance(dBm,f, ptx)
		hz = f *(10**6)
		c = (3*10**8)
		frequency_attenuation =  20*Math.log10(c/hz)-20*Math.log10(4*PI)
		jitter = 6*(Random.rand-0.5)
		gtx = 2.3
		grx = 2.3
		effective_power = ptx + gtx + grx - jitter
		n = 5
		multipath_compensation = (10*n)
		distance = 10**((effective_power-dBm)/multipath_compensation)
	end

	def lowpass(width, data_set, amount=2)

	end

	def find_ptx(power_map)
		ptx = 75.0
		sq_error = 2500
		min_error = 25
		error = 2500
		iteration = 0
		iteration_limit = 15000
		adjustment_rate = 1.0/3.0
		while(sq_error > min_error && iteration < iteration_limit)
			if iteration > 1
				if error > 0
					ptx += ptx * adjustment_rate
				else
					ptx -= ptx * adjustment_rate
				end
			end
			#puts "Guess: #{ptx}"
			error = 0
			distance_error = {}
			power_map.each_pair do |(distance, dBms)|
				distance_error[distance] = 0
				dBms.each do |dBm|
					distance_error[distance] += distance-self.distance(dBm.to_f, 2400, ptx)
				end
				distance_error[distance] = distance_error[distance]/dBms.size
			end
			distance_error.each_pair {|(distance, avg_error)| error += avg_error}
			error = error / distance_error.size
			sq_error = error ** 2
			#puts "Average Error: #{error}"
			iteration += 1
		end
		ptx
	end

	def parse_powermap_csv(filename)
		power_map = {}
		distance = 0
		CSV.foreach(filename) do |row|
			power_map[distance] = []
			row.each do |dBm|
				power_map[distance] << dBm
			end
			distance += 1
		end
		power_map
	end
end