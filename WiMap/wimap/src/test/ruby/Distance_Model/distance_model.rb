require 'csv'

class DistanceModel
	PI = 3.14
	

	def distance(dBm,f, ptx)
		hz = f *(10.0**6.0)
		c = (3.0*10.0**8.0)
		frequency_attenuation =  20*Math.log10(c/hz)-20*Math.log10(4*PI)
		jitter = 6.0*(Random.rand-0.5)
		gtx = 2.3
		grx = 2.3
		effective_power = ptx + gtx + grx - jitter
		n = 5.0
		multipath_compensation = (10.0*n)
		10.0**((effective_power-dBm)/multipath_compensation)
	end

	def find_ptx(power_map)
		ptx = 75.0
		sq_error = 2500
		max_error = 0
		error = 2500
		iteration = 0
		delta = 100
		delta_limit = 0.0001
		adjustment_rate = 1.0/3.0
		last_adjustment = :forwards
		while(delta > delta_limit)
			
			if iteration > 1
				delta = ptx
				if error > 0
					if last_adjustment == :backwards
						adjustment_rate = adjustment_rate / 2.0
					end
					ptx += ptx * adjustment_rate
					last_adjustment = :forwards
				else
					if last_adjustment == :forwards
						adjustment_rate = adjustment_rate / 2.0
					end
					ptx -= ptx * adjustment_rate
					last_adjustment = :backwards
				end
				delta = (delta - ptx)**2.0
				
			end
			error = 0
			distance_error = {}
			power_map.each_pair do |(distance, dBms)|
				distance_error[distance] = 0
				dBms.each do |dBm|
					err = distance-self.distance(dBm.to_f, 2400.0, ptx)
					if err > max_error
						max_error = err
					end
					distance_error[distance] += err
				end
				distance_error[distance] = distance_error[distance]/dBms.size
			end
			distance_error.each_pair {|(distance, avg_error)| error += avg_error}
			error = error / distance_error.size
			sq_error = error ** 2
			iteration += 1
		end
		puts "Found coeff #{ptx}"
		puts "Peak Error #{max_error}"
		puts "Average Error #{error}"
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