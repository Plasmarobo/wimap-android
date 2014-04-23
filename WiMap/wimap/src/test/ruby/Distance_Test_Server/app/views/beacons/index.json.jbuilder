json.array!(@beacons) do |beacon|
  json.extract! beacon, :id, :name, :uid
  json.url beacon_url(beacon, format: :json)
end
