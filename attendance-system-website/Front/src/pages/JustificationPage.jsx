import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { X } from 'lucide-react';

const JustificationPage = () => {
    const { studentId } = useParams();
    const navigate = useNavigate();
    const [student, setStudent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [justifications, setJustifications] = useState({});
    const [error, setError] = useState(null);

    const handleJustifier = async (absenceId) => {
        const justification = justifications[absenceId];
        if (!justification?.motif || !justification?.fichier) {
            alert("Veuillez fournir un motif et un fichier.");
            return;
        }

        try {
            const formData = new FormData();
            formData.append("motif", justification.motif);
            formData.append("fichier", justification.fichier);

            await axios.put(`http://localhost:8080/api/absences/justifier/${absenceId}`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });

            // Recharger les absences après justification
            const res = await axios.get(`http://localhost:8080/api/absences/etudiant/${studentId}`);
            setStudent(prev => ({
                ...prev,
                absences: res.data
            }));

            // Réinitialiser les champs du formulaire
            setJustifications(prev => ({
                ...prev,
                [absenceId]: {}
            }));

            alert("Justification envoyée avec succès !");
        } catch (err) {
            console.error(err);
            alert("Erreur lors de la justification");
        }
    };

    useEffect(() => {
        const fetchStudentData = async () => {
            try {
                const studentResponse = await axios.get(`http://localhost:8080/api/etudiants/${studentId}`);
                const absencesResponse = await axios.get(`http://localhost:8080/api/absences/etudiant/${studentId}`);

                setStudent({
                    ...studentResponse.data,
                    absences: absencesResponse.data
                });
            } catch (err) {
                console.error("Erreur API:", err);
                setError(err.response?.data?.message || "Erreur lors du chargement des données");
            } finally {
                setLoading(false);
            }
        };

        fetchStudentData();
    }, [studentId]);

    if (loading) return <div className="text-center py-8">Chargement...</div>;
    if (!student) return <div className="text-center py-8">Étudiant non trouvé</div>;

    return (
        <div className="bg-white p-6 rounded-lg shadow mx-auto max-w-4xl my-8">
            <div className="flex justify-between items-start mb-6">
                <div>
                    <h1 className="text-2xl font-bold">Gestion des justifications</h1>
                    <h2 className="text-xl mt-2">Absences de {student.nom}</h2>
                    <p className="text-gray-600">Classe: {student.classeId}</p>
                    <p className="text-gray-600">
                        {student.absences?.length || 0} absences (
                        {student.absences?.filter(a => a.justifiee).length || 0} justifiées)
                    </p>
                </div>
                <button onClick={() => navigate(-1)} className="text-gray-500 hover:text-gray-700">
                    <X className="w-6 h-6" />
                </button>
            </div>

            <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                    </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                    {student.absences?.map(absence => (
                        <tr key={absence.id}>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {new Date(absence.dateAbsence).toLocaleDateString('fr-FR', {
                                    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
                                })}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                    <span className={`px-2 py-1 rounded text-xs ${
                                        absence.justifiee ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                                    }`}>
                                        {absence.justifiee ? "Justifiée" : "Non justifiée"}
                                    </span>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {!absence.justifiee && (
                                    <div className="flex flex-col gap-2">
                                        <input
                                            type="text"
                                            placeholder="Motif"
                                            className="border p-1 rounded text-sm"
                                            value={justifications[absence.id]?.motif || ""}
                                            onChange={e =>
                                                setJustifications(prev => ({
                                                    ...prev,
                                                    [absence.id]: {
                                                        ...prev[absence.id],
                                                        motif: e.target.value
                                                    }
                                                }))
                                            }
                                        />
                                        <input
                                            type="file"
                                            className="border p-1 rounded text-sm"
                                            onChange={e =>
                                                setJustifications(prev => ({
                                                    ...prev,
                                                    [absence.id]: {
                                                        ...prev[absence.id],
                                                        fichier: e.target.files[0]
                                                    }
                                                }))
                                            }
                                        />
                                        <button
                                            onClick={() => handleJustifier(absence.id)}
                                            className="bg-blue-500 text-white px-3 py-1 rounded text-sm hover:bg-blue-600"
                                        >
                                            Soumettre
                                        </button>
                                    </div>
                                )}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            <div className="mt-6 pt-4 border-t">
                <button onClick={() => navigate(-1)} className="bg-gray-200 hover:bg-gray-300 px-4 py-2 rounded">
                    Retour
                </button>
            </div>
        </div>
    );
};

export default JustificationPage;
