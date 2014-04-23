require './distance_model.rb'

model = DistanceModel.new
csv_files = Dir["./*.csv"]
csv_files.each do |file|
	power_map = model.parse_powermap_csv(file)
	coeff = model.find_ptx(power_map)
	puts "File: #{file} => #{coeff}"
end