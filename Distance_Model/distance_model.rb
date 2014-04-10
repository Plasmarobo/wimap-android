require 'math'

class distance_model
	PI = 3.14
	

	def distance(dBm,f, PTx)
		hz = f *(10**6)
		c = (3*10**8)
		frequency_attenuation =  20*Math.log10(c/hz)-20*Math.log10(4*PI)
		jitter = 6*(Math.rand-0.5)
		GTx = 2.3
		GRx = 2.3
		effective_power = PTx + GTx + GRx - jitter
		n = 5
		multipath_compensation = (10*n)
		distance = 10**(effective_power-dBm)/multipath_compensation);
	end

	def lowpass(width, data_set, amount=2)

	end

	def find_PTx(power_map)
		PTx = 75
		error = 2500
		iteration = 0
		iteration_limit = 15000
		adjustment_rate = 1/3
		while(error > 3 && iteration < iteration_limit)
			if iteration > 1
				if error > 0
					PTx -= PTx * adjustment_rate
				else
					PTx += PTx * adjustment_rate
				end
			end
			puts "Guess: #{PTx}"
			error = 0
			distance_error = {}
			power_map.each_pair do |(distance, dBms)|
				distance_error[distance] = 0
				dBms.each do |dBm|
					distance_error[distance] += distance-self.distance(dBm, 2400, PTx)
				end
				distance_error[distance] = distance_error[distance]/dBms.size
			end
			distance_error.each_pair {|(distance, avg_error)| error += avg_error}
			error = error / distance_error.size
			puts "Average Error: #{error}"
			iteration += 1
		end
		PTx
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