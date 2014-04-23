json.array!(@distance_samples) do |distance_sample|
  json.extract! distance_sample, :id, :integer, :integer, :integer
  json.url distance_sample_url(distance_sample, format: :json)
end
